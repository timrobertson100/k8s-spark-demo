/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.demo;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class SparkDemo {
  public static void main(String[] args) {
    String sourceDir =
        args.length == 2 ? args[0] : "/data/ingest_spark/50c9509d-22c7-4a22-a47d-8c48425ef4a7/236";
    String targetDir = args.length == 2 ? args[1] : "/user/tim/spark-demo";

    SparkSession spark =
        SparkSession.builder()
            .appName("SparkDemo")
            .config("spark.sql.warehouse.dir", "hdfs://gbif-hdfs" + targetDir + "/warehouse2")
            .getOrCreate();

    // Difference is number of files. File size not all that significant at runtime
    // setupWarehouse(spark, sourceDir); // copies and prepares data
    // setupWarehouse2(spark, sourceDir); // copies and prepares data

    // By reading from the parquet, it is SIGNIFICANTLY quicker than using the tables produced above
    reloadWarehouse(spark, targetDir + "/warehouse2"); // loads the prepared data

    spark
        .sql(
            """
        SELECT
          v.id,
          v.verbatim as verbatim,
          b.basic as basic,
          t.temporal as temporal,
          ta.taxonomy as taxonomy,
          g.grscicoll as grscicoll,
          l.location as location
        FROM verbatim v
          LEFT JOIN basic b ON v.id = b.id
          LEFT JOIN temporal t ON v.id = t.id
          LEFT JOIN taxonomy ta ON v.id = ta.id
          LEFT JOIN grscicoll g ON v.id = g.id
          LEFT JOIN location l ON v.id = l.id
        """)
        //        .coalesce(300)
        .write()
        .format("parquet")
        .mode("overwrite")
        .saveAsTable("output");
  }

  private static void reloadWarehouse(SparkSession spark, String sourceDir) {
    spark.read().parquet(sourceDir + "/basic").createOrReplaceTempView("basic");
    spark.read().parquet(sourceDir + "/grscicoll").createOrReplaceTempView("grscicoll");
    spark.read().parquet(sourceDir + "/location").createOrReplaceTempView("location");
    spark.read().parquet(sourceDir + "/taxonomy").createOrReplaceTempView("taxonomy");
    spark.read().parquet(sourceDir + "/temporal").createOrReplaceTempView("temporal");
    spark.read().parquet(sourceDir + "/verbatim").createOrReplaceTempView("verbatim");
  }

  private static void setupWarehouse(SparkSession spark, String sourceDir) {
    rewriteToWarehouse(spark, sourceDir + "/basic", "basic", 100, true, false);
    rewriteToWarehouse(spark, sourceDir + "/grscicoll", "grscicoll", 10, false, true);
    rewriteToWarehouse(spark, sourceDir + "/location", "location", 100, true, false);
    rewriteToWarehouse(spark, sourceDir + "/taxonomy", "taxonomy", 100, true, false);
    rewriteToWarehouse(spark, sourceDir + "/temporal", "temporal", 50, true, false);
    rewriteToWarehouse(spark, sourceDir + "/verbatim", "verbatim", 250, true, false);
  }

  private static void setupWarehouse2(SparkSession spark, String sourceDir) {
    rewriteToWarehouse(spark, sourceDir + "/basic", "basic", 300, true, false);
    rewriteToWarehouse(spark, sourceDir + "/grscicoll", "grscicoll", 300, false, true);
    rewriteToWarehouse(spark, sourceDir + "/location", "location", 300, true, false);
    rewriteToWarehouse(spark, sourceDir + "/taxonomy", "taxonomy", 300, true, false);
    rewriteToWarehouse(spark, sourceDir + "/temporal", "temporal", 300, true, false);
    rewriteToWarehouse(spark, sourceDir + "/verbatim", "verbatim", 300, true, false);
  }

  // Reads, repartition and replace the table in warehouse
  private static void rewriteToWarehouse(
      SparkSession spark,
      String sourceDir,
      String tableName,
      int partitionCount,
      boolean expandToKVP,
      boolean renameToKVP) {
    spark.sql(String.format("DROP TABLE IF EXISTS %s", tableName));

    Dataset<Row> input;

    if (expandToKVP)
      input =
          spark
              .read()
              .parquet(sourceDir)
              .as(Encoders.tuple(Encoders.STRING(), Encoders.STRING()))
              .toDF("id", tableName);
    else if (renameToKVP) input = spark.read().parquet(sourceDir).toDF("id", tableName);
    else input = spark.read().parquet(sourceDir);

    input
        .repartition(partitionCount)
        .write()
        .format("parquet")
        .mode("overwrite")
        .saveAsTable(tableName);
  }
}
