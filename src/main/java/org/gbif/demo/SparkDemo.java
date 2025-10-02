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

import org.apache.spark.sql.SparkSession;

public class SparkDemo {
  public static void main(String[] args) {
    String sourceDir =
        args.length == 2 ? args[0] : "/data/ingest_spark/50c9509d-22c7-4a22-a47d-8c48425ef4a7/236";
    String targetDir = args.length == 2 ? args[1] : "/user/tim/spark-demo";

    SparkSession spark =
        SparkSession.builder()
            .appName("SparkDemo")
            .config("spark.sql.warehouse.dir", "hdfs://gbif-hdfs" + targetDir + "/warehouse")
            .getOrCreate();

    setupWarehouse(spark, sourceDir);
  }

  private static void setupWarehouse(SparkSession spark, String sourceDir) {
    rewriteToWarehouse(spark, sourceDir + "/extended_records", "verbatim", 250);
    rewriteToWarehouse(spark, sourceDir + "/basic", "basic", 250);
  }

  // Reads, repartition and replace the table in warehouse
  private static void rewriteToWarehouse(
      SparkSession spark, String sourceDir, String tableName, int partitionCount) {
    spark.sql(String.format("DROP TABLE IF EXISTS %s", tableName));
    spark
        .read()
        .parquet(sourceDir)
        .repartition(partitionCount)
        .write()
        .format("parquet")
        .mode("overwrite")
        .bucketBy(10, "id")
        .sortBy("id")
        .saveAsTable(tableName);
  }
}
