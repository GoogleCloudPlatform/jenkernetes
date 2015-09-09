# Jenkernetes

This repository provides the resources and instuctions for running a Jenkins server and individual Jenkins slaves on a Kubernetes Cluster. It also provides a plugin that allows you to administer the Kubernetes Cluster from within Jenkins workflow scripts, (and WIP administering the Kubernetes Cluster from within the Jenkins Webserver)

## Repo Overview

setup/master contains most of the setup instructions for getting Jenkins master running on a Kubernetes cluster.
setup/slave contains most of the setup instructions for connecting slaves in a replication controller to that master.
examples/ contains miscellanious Jenkins plugins, and Docker images we think are useful.
 
examples/python_slave contains an image for a Python slave with Python and pip installed.
examples/ssh contains images for setting up an nginx SSL proxy.
examples/workflowscript contains some scripts to access the Kubernetes API from Jenkins Workflow scripts.

## Compared to other Jenkins/Kubernetes projects

Currently this project does not use the Kubernetes plugin, although it probably eventually will. Previously, the Kubernetes plugin did not run the actual Jenkins master in the Kubernetes cluster, although that's soon becoming an option.

https://github.com/GoogleCloudPlatform/kube-jenkins-imager is an example of two teams working on the same thing. That repo includes a solutions article, and a really good setup script to get up and running. 

# Quickstart

## PreReqs
 
* Install [Google Cloud SDK](https://cloud.google.com/sdk/)
* Install [Docker](https://www.docker.com/)
* Create a project on the [Google Cloud Console](https://console.developers.google.com/)

## Create a Cluster (see [Container Engine Getting Started](https://cloud.google.com/container-engine/docs/getting-started) for more detail)

Make sure your gcloud tool is up to date.
* `gcloud components update beta`
* `gcloud components update kubectl`

Set the project the gcloud command will use.
* `gcloud config set project PROJECT_ID`

Create the cluster.
* `gcloud beta container clusters create jenkins`
Make sure the kubectl is using this new cluster.
* `kubectl config use-context gke_$(gcloud config list | grep project | cut -f 3 -d' ')_${ZONE}_${CLUSTER_NAME}`

## Install Jenkins Master (see setup/master/README.md for more detail)

This is a helper script that creates a temporary GCE instance in order to format a new persistent disk that Jenkins will store job configurations on.
* `./create_and_format_disk.sh`
This line creates the Jenkins service.
* `kubectl create -f setup/master/service_config.json`
* Run `gcloud compute instances list` and look for YOUR_CLUSTER_ID between an instance name gke-YOUR-CLUSTER-ID-node-
This line makes sure that web traffic to port 8080 is allowed.
* `gcloud compute firewall-rules create jenkins-webserver --allow TCP:8080 --target-tags gke-<YOUR-CLUSTER-ID>-node`
This line creates a pod with Jenkins install to back the Jenkins service.
* `kubectl create -f setup/master/pod_config.json`
* `gcloud compute forwarding-rules list`
* Go to *JENKINS*:8080 in browser , where Jenkins is the IP from the
forwarding rules list

## Install Jenkins Slaves (see setups/slave/README.md for more detail)
 This line creates a replacation controller using the slave image created in one of the examples.
* `kubectl create -f examples/python_slave/basic-config-rc.json`
This line scales that replication controller to 5 slave pods.
* `kubectl scale --replicas=5 rc my-cloud-sdk-python-slave-rc`
