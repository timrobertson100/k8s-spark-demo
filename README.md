### Spark hive Demo

A deliberately simple demo of Spark running SQL queries against Hive registered tables.

To build:

```
mvn clean package
```

Copy the built file to HDFS (TODO: document how) and modify the `app.yaml` accordingly.

To run:
```
kubectl apply -f app.yaml 
```
