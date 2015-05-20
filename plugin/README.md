# Workflow Steps for Kubernetes

This plugin provides [workflow steps](https://github.com/jenkinsci/workflow-plugin/) for CRUD operations on Kubernetes Resources.

## Usage

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

   * (Optional) This plugin is not yet available through the Jenkins UI, as such if you want to run this plugin (rather than just Jenkins on Kubernetes) use an image that manually includes the ".hpi" file. You can either use `elibixby/jenkins_master`, or build it yourself by running

     ```
cd plugin
mvn clean package
cp target/kubernetes-workflow-steps.hpi setup/image/
sudo docker build -t <image-name-of-your-choice> setup/image
sudo docker push <image-name-of-your-choice>
```
2. (Recommended) Select a method of persisting your Jenkins configuration and jobs. For example, you could use a [Google Compute Engine Persistent Disk]() or [Google Cloud Storage]()

3. (Recommended) Select a method of authenticating to your Jenkins Server. There are several plugins which can be installed once your server is running. Alternatively you can embed a proxy like [nginx]() in your Kubernetes cluster.

4. Create a [Google Container Engine Cluster](https://cloud.google.com/container-engine/docs/clusters/operations#before_you_begin). This walkthrough will make extensive use of the gcloud command line tool, so it is recommend you follow the relevant instructions for gcloud.

   Now is also a good time to set up your gcloud defaults:

   ```
gcloud config set compute/ZONE <zone-of-your-cluster>
gcloud config set container/cluster <name-of-your-cluster>
```

5. Create a [Kubernetes Service](https://github.com/GoogleCloudPlatform/kubernetes/blob/master/docs/services.md) which will serve as a stable HTTP endpoint for your Jenkins Server.

   gcloud comes bundled with [kubectl](https://github.com/GoogleCloudPlatform/kubernetes/blob/master/docs/kubectl.md) which we will use to administer our cluster.

   ```
kubectl create -f plugin/setup/service_config.json
```

   Since Jenkins is a webserver, we also need to create a firewall rule, so our service is accesible from the outside

   ```
gcloud compute firewall-rules create jenkins-webserver --allow TCP:8080 --target-tags k8s-<name-of-your-cluster>-node
```

6. (Optional) Next we need to allow the Jenkins server to authenticate to the Kubernetes master service. Currently there is no easy way to do this, although a faster solution is forthcoming. If you don't care about running this plugin, and are simpling trying to run Jenkins in a Kubernetes pod, you can skip this step.

   The script `fetch_bearer_token.sh` fetches the bearer token your nodes use to authenticate to the Kubernetes master, encodes the file and creates a [Secret](https://github.com/GoogleCloudPlatform/kubernetes/blob/master/docs/secrets.md) which you can mount on your pods.

   ```
./plugin/setup/fetch_bearer_token.sh <name-of-your-cluster>
```

   (Make sure to say yes when prompted to delete `kubernetes_auth` to avoid accidentally leaking these sensitive credentials)

6. Create the pod that runs your Jenkins Server. (Alternatively if you want to maximize uptime in the event of pod deletion, you can create a [replication controller](https://github.com/GoogleCloudPlatform/kubernetes/blob/master/docs/replication-controller.md) of size 1)

   * If you want to use your own image for your jenkins server, feel free to change the `image` value in `setup/pod_config.json`
   * If you don't want to run this plugin, simply remove the `volume` and `volumeMount` sections from `setup/pod_config.json`

   ```
kubectl create -f plugin/setup/pod_config.json
```

You should now be able to access your Jenkins webserver! Find the IP by running:

```
gcloud compute forwarding-rules list
```

Go to YOUR-IP:8080 in your webrowser!

If you installed the plugin as part of this walkthrough, head on over to the [examples](plugin/examples) directory to see some of the workflow scripts you can run with this plugin, or head over to the [client](client/) directory to see how you can make your Kubernetes Pods into Jenkins Slaves (including ones you create with this plugin!).


## Contributing changes

* See [CONTRIBUTING.md](CONTRIBUTING.md)


## Licensing

* See [LICENSE](LICENSE)
