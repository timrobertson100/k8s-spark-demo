### Spark hive Demo
This demo assumes you have access to a namespace with running HDFS-, hive- and spark-clusters.

A deliberately simple demo of Spark running SQL queries against Hive registered tables.

To build:

```
mvn clean package
```

Copy the built file to HDFS and modify the `mainApplicationFile` location `app.yaml` accordingly to point at the application Jar file.

To copy a file to HDFS, you can use the helper script `./util/transfer-jar.sh`. An example how to use it:
```
./util/transfer-jar.sh my-awsome-app.jar
```

The defaults is configured for the HDFS in the dev2 env but allows for the user to provide arguments to change which HDFS to use. As illustrated below:

```
./util/transfer-jar.sh my-awsome-app.jar some-namenode-0 some-namepsace
```

To run:
```
kubectl apply -f app.yaml 
```

## Spark shell demo
As a proof of concept, a small script has been develop to enable utilizing spark-shell within the Kubernetes cluster. It is a prequirement that the spark-client is deployed in the namespace. The script is called like the following:
```
./util/start-spark-shell-in-k8.sh <kubernetes-master-url> <namespace> <spark-client-pod-name>
```
It is possible to pass the size of the executors as well:
```
./util/start-spark-shell-in-k8.sh <kubernetes-master-url> <namespace> <spark-client-pod-name> <number-of-executors> <number-of-cores> <size-of-memory>
```
To give realistic example:
```
./util/start-spark-shell-in-k8.sh https://localhost:6443 my-namespace spark-client-43hfcc34fd-8cnxm 4 8 10g
```
