# Run Jenkins in Kubernetes

This setup describes the setup for running a Jenkins webserver on Google Container Engine.

## Workflow Steps for Kubernetes

You also will have the option of installing the Workflow Steps for Kubernetes plugin. This plugin provides [workflow steps](https://github.com/jenkinsci/workflow-plugin/) for CRUD operations on Kubernetes Resources.

### Usage

This plugin enables the following workflow steps

```
kube_create(resource: a_resource, payload: your_payload)
kube_get(resource: a_resource, name(optional): "resource_name")
kube_update(resource: a_resource, payload: your_payload, name: "resource_name")
kube_delete(resource: a_resource, resource: "resource_name")
```

* "a\_resource" is the name of the Kubernetes resource on which you want to perform the operation (e.g. "pods", "services", replicationcontrollers" etc). For a full list of available resources, read the [Kubernetes API Spec](http://kubernetes.io/third_party/swagger-ui/#!/v1beta3/).
* "your\_payload" is a groovy map which corresponds to the JSON object you would send to the API.
* Each of these methods returns a groovy map corresponding to the JSON output of the API method.

## Prerequisites

NOTE: The "Setup" assumes you are running your Kubernetes Cluster on Google Cloud Platform. However Kubernetes is platform agnostic. To contribute instructions for other Cloud Platforms, see the instructions in [CONTRIBUTING.md](CONTRIBUTING.md)

## Setup

This section will walk you through the basics of running Jenkins on a Kubernetes Pod. This is useful far beyond this plugin, and as such the steps only necessary for this plugin are marked "Optional".

1. You must select a Docker image which runs your Jenkins server. This can be the LTS image available on [Docker Hub](https://registry.hub.docker.com/_/jenkins/) or a custom image (although extending from an official Jenkins image is recommended).

   * (Optional) This plugin is not yet available through the Jenkins UI, as such if you want to run this plugin (rather than just Jenkins on Kubernetes) use an image that manually includes the ".hpi" file. You can build it yourself by running

     ```
mvn clean package
cp target/kubernetes-workflow-steps.hpi setup/master/image/
sudo docker build -t <image-name-of-your-choice> setup/image
sudo docker push <image-name-of-your-choice>
```

2. (Recommended) Select a method of authenticating to your Jenkins Server. There are several plugins which can be installed once your server is running. Alternatively you can embed a proxy like [nginx]() in your Kubernetes cluster.

3. Create a [Google Container Engine Cluster](https://cloud.google.com/container-engine/docs/clusters/operations#before_you_begin). This walkthrough will make extensive use of the gcloud command line tool, so it is recommend you follow the relevant instructions for gcloud.

   Now is also a good time to set up your gcloud defaults:

   ```
gcloud config set compute/zone <zone-of-your-cluster>
gcloud config set container/cluster <name-of-your-cluster>
```

4. Since the Jenkins docker image stores configuration on a volume `/var/jenkins_home` we can mount the persistent disk on the image and our configuration will be persisted. First we have to create a [Google Compute Engine Persistent Disk]() and add it to our cluster.

    ```
gcloud compute disks create jenkins-config --type pd-ssd --size 10GB
gcloud compute instances create temp-writer --disk name=jenkins-config,device-name=jenkins-config \
	                    --metadata startup-script="sudo mkdir /mnt/jenkins-config && sudo /usr/share/google/safe_format_and_mount /dev/disk/by-id/google-jenkins-config /mnt/jenkins-config"
gcloud compute ssh temp-writer --command "sudo umount /mnt/jenkins-config"
gcloud compute instances detach-disk temp-writer --disk jenkins-config
gcloud compute instances delete temp-writer
```

5. Create a [Kubernetes Service](https://github.com/GoogleCloudPlatform/kubernetes/blob/master/docs/services.md) which will serve as a stable HTTP endpoint for your Jenkins Server.

   gcloud comes bundled with [kubectl](https://github.com/GoogleCloudPlatform/kubernetes/blob/master/docs/kubectl.md) which we will use to administer our cluster.

   ```
kubectl create -f setup/master/service_config.yaml
```

6. Create the pod that runs your Jenkins Server.
    Inspect the file `setup/master/pod_config.yaml` and change the "image" field to the image you built and pushed in step 1. This image assumes you are persisting your configuration via Compute Engine Persistent Disk as described in step 4.

    ```
kubectl create -f setup/master/pod_config.yaml
```

7. Since Jenkins runs a webserver, we also need to create a firewall rule to access our service from the outside.
   Find the cluster-id given to your Cluster by running `gcloud compute instances list`.		
   
   You should see at least one instance listed with the format:		
   ```		
   NAME				 							ZONE          MACHINE_TYPE  PREEMPTIBLE INTERNAL_IP  EXTERNAL_IP     STATUS		
   gke-<YOUR-CLUSTER-ID>-<FOUR-CHARACTER-NODE-ID>  us-central1-c n1-standard-1             10.240.92.67 130.211.185.204 RUNNING		
   ```		
   Use the following command to create a firewall rule that allows incoming traffic on port 8080 (the default for the Jenkins webserver) to any node in your cluster:
   ```		
   gcloud compute firewall-rules create jenkins-webserver --allow TCP:8080 --target-tags gke-<YOUR-CLUSTER-ID>		
   ```
 
8. You should now be able to access your Jenkins webserver! Find the IP under the **EXTERNAL-IP** column by running the following command:
   ```
kubectl get services
```

   Alternatively if you want to maximize uptime in the event of pod deletion, you can create a [replication controller](https://github.com/GoogleCloudPlatform/kubernetes/blob/master/docs/replication-controller.md) of size 1

   ```
   kubectl create -f setup/master/replication_controller_config.yaml
   ```

Now that you have a Jenkins master running in your Kubernetes cluster, check out [slave setup](setup/slave/) to find out how to run any Docker image as a Jenkins slave in your cluster!

## Contributing changes

* See [CONTRIBUTING.md](CONTRIBUTING.md)


## Licensing

* See [LICENSE](LICENSE)
