#!/bin/bash
KUBE_MASTER="${1:-https://localhost:6443}"
NAMESPACE="${2:-gbif-develop}"
POD_NAME="${3:-spark-shell}"
INSTANCES="${4:-4}"
CORES="${5:-6}"
MEM="${6:-10g}"
kubectl exec -n ${NAMESPACE} -it pod/${POD_NAME} -- sh -c "\
/stackable/spark/bin/spark-shell \
--master k8s://$KUBE_MASTER \
--deploy-mode client  \
--conf spark.kubernetes.namespace="${NAMESPACE}"  \
--conf spark.kubernetes.container.image="docker.stackable.tech/stackable/spark-k8s:3.3.0-stackable23.4.0" \
--conf spark.kubernetes.container.image.pullPolicy=IfNotPresent  \
--conf spark.kubernetes.authenticate.serviceAccountName="spark-client"  \
--conf spark.kubernetes.driver.pod.name="${POD_NAME}"  \
--conf spark.executor.instances="${INSTANCES}"  \
--conf spark.executor.memory="${MEM}" \
--conf spark.executor.cores="${CORES}" \
--conf spark.driver.memory="2g" \
--conf spark.driver.cores="2" \
--conf spark.driver.host="spark-client" \
--conf spark.driver.port="7078" \
--conf spark.blockManager.port="7089" \
--conf spark.driver.bindAddress="0.0.0.0" \
--conf spark.kubernetes.driver.podTemplateFile=/etc/template/pod-template.yaml \
--conf spark.kubernetes.executor.podTemplateFile=/etc/template/pod-template.yaml"