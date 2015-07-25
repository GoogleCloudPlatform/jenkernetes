# Jenkernetes

This repository provides the resources and instuctions for running a Jenkins server and individual Jenkins slaves on a Kubernetes Cluster. It also provides a plugin that allows you to administer the Kubernetes Cluster from within Jenkins workflow scripts, (and WIP administering the Kubernetes Cluster from within the Jenkins Webserver)

# Quickstart

## PreReqs
 
* Install [Google Cloud SDK](https://cloud.google.com/sdk/)
* Install [Docker](https://www.docker.com/)
* Create a project on the [Google Cloud Console](https://console.developers.google.com/)

## Create a Cluster (see [Container Engine Getting Started](https://cloud.google.com/container-engine/docs/getting-started) for more detail)

* `gcloud components update beta`
* `gcloud components update kubectl`
* `gcloud config set project PROJECT_ID`
* `gcloud config set compute/zone us-central1-b`
* `gcloud beta container clusters create jenkins`
* `gcloud config set container/cluster jenkins`

## Install Jenkins Master (see setup/master/README.md for more detail)

* `./create_and_format_disk.sh`
* `kubectl create -f setup/master/service_config.json`
* Run `gcloud compute instances list` and look for YOUR_CLUSTER_ID between an instance name gke-YOUR-CLUSTER-ID-node-
* `gcloud compute firewall-rules create jenkins-webserver --allow TCP:8080 --target-tags gke-<YOUR-CLUSTER-ID>-node`
* `kubectl create -f setup/master/pod_config.json`
* `gcloud compute forwarding-rules list`
* Go to *JENKINS*:8080 in browser

## Install Jenkins Slaves (see setups/slave/README.md for more detail)
* `kubectl create -f examples/python_slave/basic-config-rc.json`
* `kubectl scale --replicas=5 rc my-cloud-sdk-python-slave-rc`
