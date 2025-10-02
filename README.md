### Spark Demo

Tests of joins 

To build:

```
mvn clean package
```

Copy the jar up to HDFS
```
rclone copy target/spark-demo-1.0.0-3.5.6.jar test:/user/tim 
```

To run:
```
kubectl apply -f app.yaml 
```

To remove or kill:
```
kubectl delete -f app.yaml 
```