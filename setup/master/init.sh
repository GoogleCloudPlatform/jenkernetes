#!/bin/bash
kubectl delete pods jenkinsmaster
cd ~/CI/jenkernetes
mvn clean package
cp target/kubernetes-workflow-steps.hpi setup/master/image/
cd setup/master
sudo docker build -t gcr.io/jenkernetes-cloud-devrel/jenkins-master image/
gcloud docker push gcr.io/jenkernetes-cloud-devrel/jenkins-master
kubectl create -f pod_config.json
