## Kubernetes-Swarm-Client

This client is a thin wrapper around the [jenkins-swarm-plugin](https://wiki.jenkins-ci.org/display/JENKINS/Swarm+Plugin), which uses the environment variables provided by [Kubernetes Services](https://github.com/GoogleCloudPlatform/kubernetes/blob/master/docs/services.md) as the master endpoint automatically. As such, this client assumes that:

1. Your container will be run in a Kubernetes Pod.
2. The cluster in which your pod will be run (already) has a service named "jenkins" which is the http endpoint for a Jenkins Server. For an example of how to do this check out the [kubernetes-workflow-steps plugin](/plugin/README.md)
3. The Jenkins server behind the service has the Jenkins-swarm-plugin installed.

A container which runs this client and satisfies these conditions will automatically connect to the Jenkins master as a slave. Check out the original swarm-plugin for a full list of optional flags.

## Usage

To generate the runnable jar, simply run `mvn clean package`

There are two main ways of using this client.

### Extension

1. Extend the desired Docker image adding the jar as an ENTRYPOINT.
For example, you could use the [example Dockerfile](client/examples/image/Dockerfile) to create a Jenkins slave with gcloud SDK installed.
2. Build and push the image
```
sudo docker build -t <mydockerhubid>/jenkins-cloud-sdk-slave client/examples/image/
sudo docker push <mydockerhubid>/jenkins-cloud-sdk-slave
```
3. Create a pod with the given image.
```
kubectl create -f client/examples/basic_config.json
```

This method has the advantage of leading to (relatively simple) pod configurations, but is frustrating because you must build and push a new image everytime you want to use a new Docker image as a Jenkins slave, or update an existing image. Although you can make these tasks part of your build with plugins like the [Docker Build Step Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Docker+build+step+plugin), this adds extra time to your builds, especially since it prevents Kubernetes from effectively cacheing your images.

The next method allows you to use Docker images as jenkins slaves out-of-the-box, but results in more complex pod configurations.

### Volume Mounting

1. Add the Jar to a [volume](https://github.com/GoogleCloudPlatform/kubernetes/blob/master/docs/volumes.md) in your cluster:
Depending on your cloud provider, these instructions will vary. As elsewhere, we use Google Container Engine as an example, in this case creating a Google Compute Engine Persistent Disk. We use the smallest size available, and a persistent disk to avoid iops limitations when the volume is mounted on a large number of vms.
```
gcloud compute disks create swarm-client --type pd-ssd --size 10GB
gcloud compute instances create temp-writer --disk name=swarm-client,device-name=swarm-client \
					    --metadata startup-script="sudo mkdir /mnt/swarm-client && sudo /usr/share/google/safe_format_and_mount /dev/disk/by-id/google-swarm-client /mnt/swarm-client"
gcloud compute copy-files ./client/target/kubernetes-swarm-client-jar-with-dependencies.jar root@temp-writer:/mnt/swarm-client/swarm-client.jar
gcloud compute ssh temp-writer --command "sudo umount /mnt/swarm-client"
gcloud compute instances delete temp-writer
```
NOTE: You only need to run this once per cluster. After the volume is created, it can be mounted on any number of containers as a read-only volume.
2. Now we can make out-of-the-box images into Jenkins Slaves by mounting our volume "swarm-client" on the container, and running the script.
```
kubectl create -f client/examples/volume-mounting-config.json
```

## Contributing changes

* See [CONTRIBUTING.md](CONTRIBUTING.md)

## Licensing

* See [LICENSE](LICENSE)
