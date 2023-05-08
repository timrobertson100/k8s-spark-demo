### Spark hive Demo

A deliberately simple demo of Spark running SQL queries against Hive registered tables.

To build:

```
mvn clean package
```

Copy the built file to HDFS (TODO: document how) and modify the `mainApplicationFile` location `app.yaml` accordingly to point at the applicaiton Jar file.

To run:
```
kubectl apply -f app.yaml 
```
