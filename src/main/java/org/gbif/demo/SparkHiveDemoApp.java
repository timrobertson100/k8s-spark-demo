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
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class SparkHiveDemoApp {
  public static void main(String[] args) {

    String hive = args.length == 3 ? args[0] : "thrift://sc4n2:30420";
    String source = args.length == 3 ? args[1] : "downloads.dev2_occurrence";
    String target = args.length == 3 ? args[2] : "downloads.test_dataset_to_delete";

    SparkSession spark =
        SparkSession.builder()
            .appName("SparkHiveDemoApp")
            .config("hive.metastore.uris", hive)
            .enableHiveSupport()
            .getOrCreate();

    Dataset<Row> df = spark.sql("SELECT * FROM " + source + " WHERE classKey=212"); // birds
    System.err.println("Query produced " + df.count() + " records");
    df.write().saveAsTable(target);
  }
}
