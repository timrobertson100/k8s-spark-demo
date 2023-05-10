### Spark hive Demo

A deliberately simple demo of Spark running SQL queries against Hive registered tables.

To build:

```
mvn clean package
```

Copy the built file to HDFS and modify the `mainApplicationFile` location `app.yaml` accordingly to point at the applicaiton Jar file.

To copy a file to HDFS, you can use the helper script `./util/transfer-jar.sh`. An example how to use it:
```
./util/transfer-jar.sh my-awsome-app.jar
```

The defaults the options for the hdfs in the dev2 env but allows for the user to provide arguments to change which hdfs to use. As illustrated below:

```
./util/transfer-jar.sh my-awsome-app.jar some-namenode-0 some-namepsace
```

To run:
```
kubectl apply -f app.yaml 
```
