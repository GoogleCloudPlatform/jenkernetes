## Slave Setup

To connect to a Docker image to the Jenkins master as a slave, we use the [Jenkins Swarm Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Swarm+Plugin). Assuming that you have the swarm plugin installed on master (if you used the image from `setup/master/image/` then you do), there are two different ways to run the swarm client on your pod.


### Extension

1. Extend the desired Docker image adding the jar as an ENTRYPOINT. For example, you could use the [provided Dockerfile](image/Dockerfile) to create a Jenkins slave with gcloud SDK installed.
2. Build and push the image

   ```
sudo docker build -t <mydockerhubid>/jenkins-cloud-sdk-slave setup/slave/image/
sudo docker push <mydockerhubid>/jenkins-cloud-sdk-slave
```
3. Create a pod with the given image.

   NOTE: We use [DNS](https://github.com/GoogleCloudPlatform/kubernetes/blob/master/docs/dns.md) provided inside the Kubernetes cluster to find the Jenkins master. In the example configuration we assume a Jenkins service with the name "jenkins", which will be reachable within the pod at "http://jenkins:8080".

   ```
kubectl create -f examples/jsonapi/basic-config.json
```

This method has the advantage of leading to (relatively simple) pod configurations, but is frustrating because you must build and push a new image everytime you want to use a new Docker image as a Jenkins slave, or update an existing image. Although you can make these tasks part of your build with plugins like the [Docker Build Step Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Docker+build+step+plugin), this adds extra time to your builds, especially since it prevents Kubernetes from effectively cacheing your images.

The next method allows you to use Docker images as jenkins slaves out-of-the-box, but results in more complex pod configurations.

### Volume Mounting

1. Add the Jar to a [volume](https://github.com/GoogleCloudPlatform/kubernetes/blob/master/docs/volumes.md) in your cluster:

   ```
gcloud compute disks create swarm-client --type pd-ssd --size 10GB
gcloud compute instances create temp-writer --disk name=swarm-client,device-name=swarm-client \
	                    --metadata startup-script="sudo mkdir /mnt/swarm-client && sudo /usr/share/google/safe_format_and_mount /dev/disk/by-id/google-swarm-client /mnt/swarm-client"
gcloud compute copy-files setup/slave/image/swarm-client.jar root@temp-writer:/mnt/swarm-client/swarm-client.jar
gcloud compute ssh temp-writer --command "sudo umount /mnt/swarm-client"
gcloud compute instances detach-disk temp-writer --disk swarm-client
gcloud compute instances delete temp-writer
```
    NOTE: You only need to run this once per cluster. After the volume is created, it can be mounted on any number of containers as a read-only volume.

2. Now we can make out-of-the-box images into Jenkins Slaves by mounting our volume "swarm-client" on the container, and running the script.

   ```
kubectl create -f examples/jsonapi/volume-mounting-config.json
```

### Workflow Scripts

If you installed the Workflow Steps Plugin on your master, check out the example workflow scripts at [examples/workflowscripts/](examples/workflowscripts/).

## Contributing changes

* See [CONTRIBUTING.md](CONTRIBUTING.md)

## Licensing

* See [LICENSE](LICENSE)
