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

import org.gbif.hadoop.compress.d2.D2Codec;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;

public class SparkHiveDemoApp {
  public static void main(String[] args) {

    String source = args.length == 2 ? args[0] : "downloads.occurrence";
    String target = args.length == 2 ? args[1] : "downloads.test_dataset_to_delete";

    SparkSession spark =
        SparkSession.builder().appName("SparkHiveDemoApp").enableHiveSupport().getOrCreate();

    // Demonstrate a CTAS registered in Hive, stored as Deflate2 TSVs
    SparkConf conf = spark.sparkContext().conf();
    conf.set("hive.exec.compress.output", "true");
    conf.set("mapred.output.compression.codec", D2Codec.class.getName());
    spark.sql(
        "CREATE table "
            + target
            + " "
            + "ROW FORMAT DELIMITED FIELDS TERMINATED BY '\\t' "
            + "AS SELECT gbifId, scientificName FROM "
            + source
            + " WHERE classKey>200");
  }
}
