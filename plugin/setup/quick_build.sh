#!/bin/bash

cd ~/CI/jenkins/kubernetes-workflow-steps/
mvn clean package
cp target/kubernetes-workflow-steps.hpi ~/CI/jenkins/jenkins-pushtodeploy/masterimage/kubernetes-workflow-steps.hpi
cd ~/CI/jenkins/jenkins-pushtodeploy/masterimage/
sudo docker build -t elibixby/jenkins_master .
sudo docker push elibixby/jenkins_master:latest
gcloud alpha container kubectl delete pods jenkinsmaster
gcloud alpha container kubectl create -f pod_config.json
