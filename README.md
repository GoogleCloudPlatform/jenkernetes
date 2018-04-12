![status: inactive](https://img.shields.io/badge/status-inactive-red.svg)

This project is no longer actively developed or maintained.

For new work on this check out [Jenkins on Kubernetes Engine](https://cloud.google.com/solutions/jenkins-on-kubernetes-engine).

# Jenkernetes

This repository provides the resources and instructions for running a Jenkins server and individual Jenkins slaves on a Kubernetes Cluster. It also provides a plugin that allows you to administer the Kubernetes Cluster from within Jenkins workflow scripts, (and WIP administering the Kubernetes Cluster from within the Jenkins Webserver)

Please remember you will be billed for any Container Engine instances. See 
Container Engine documentation for pricing levels.

## Note

Originally this project ran tests in replication controllers, where you would create a new replication controller everytime you had a new image you wanted to run tests on. Going forward, this project will migrate to a single builder image type, which can then pull down its own images and build within that. This will make it so you don't have to interface with Kubernetes in order to add new build images, and (hopefully) will allow Docker builds to take place in the test jobs.

## Repo Overview

* `setup/master` contains most of the setup instructions for getting Jenkins 
master running on a Kubernetes cluster.
* `setup/slave` contains most of the setup instructions for connecting slaves in
 a replication controller to that master.
examples/ contains miscellanious Jenkins plugins, and Docker images we think are useful.
* examples/python_slave contains an image for a Python slave with Python and 
 pip installed.
* examples/ssh contains images for setting up an nginx SSL proxy.
* examples/workflowscript contains some scripts to access the Kubernetes API 
from Jenkins Workflow scripts.

## Compared to other Jenkins/Kubernetes projects

This repo was created to open source the effort of a specific team using
Jenkins on Kubernetes to run integration tests. Below are some other
resources for those interesting in running Jenkins on Kubernetes.

There is an official [Jenkins Kubernetes plugin]
(https://wiki.jenkins-ci.org/display/JENKINS/Kubernetes+Plugin). Originally, 
we didn't use it since we wanted to run Jenkins master in Kubernetes (and not
just the slaves), although as the plugin improves we will likely migrate to it.

https://github.com/GoogleCloudPlatform/kube-jenkins-imager was a separate 
effort to accomplish the same goal. It's associated with a useful tutorial, and
has an easy cluster_up.sh and cluster_down.sh command to get a fully running
Jenkins cluster in just one line.

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

Make sure kubectl is using this new cluster.

* `gcloud beta container clusters get-credentials jenkins`

## Install Jenkins Master (see setup/master/README.md for more detail)

This is a helper script that creates a temporary GCE instance in order to format a new persistent disk that Jenkins will store job configurations on.

* `./create_and_format_disk.sh`

### Creating GKE objects
This line creates the Jenkins service.
* `kubectl create -f setup/master/service_config.yaml`

This line creates a pod with Jenkins install to back the Jenkins service.

* `kubectl create -f setup/master/pod_config.yaml`

### Configuring GKE's firewall
Run `gcloud compute instances list` to identify your instance names. Then, run `gcloud compute instances describe <YOUR-INSTANCE-NAME>` and look for YOUR_CLUSTER_TAG of the form `gke-<YOUR-CLUSTER-TAG>-<NODE-ID>`.		
		
This line makes sure that web traffic to port 8080 is allowed.

* `gcloud compute firewall-rules create jenkins-webserver --allow TCP:8080 --target-tags gke-<YOUR-CLUSTER-TAG>-node`		

### Viewing your Jenkins instance
Creating the service automatically assigns it an external (ephemeral) IP, which we can find by listing Kubernetes services.

* `kubectl get services`

Go to `<YOUR-JENKINS-IP>:8080` in your browser, where `YOUR-JENKINS-IP` is the IP under the **EXTERNAL-IP** column.

## Install Jenkins Slaves (see setups/slave/README.md for more detail)

This line creates a replication controller using the slave image created in one of the examples.

* `kubectl create -f examples/python_slave/basic-config-rc.json`

This line scales that replication controller to 5 slave pods.

* `kubectl scale --replicas=5 rc my-cloud-sdk-python-slave-rc`


## Delete The Cluster

To stop billing, make sure you delete the cluster when you are finished.

* `gcloud beta container clusters delete jenkins`
