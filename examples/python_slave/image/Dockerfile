FROM google/cloud-sdk
ADD swarm-client.jar /lib/
apt-get -y update && apt-get -y install git python-pip
ENTRYPOINT ["java", "-jar", "/lib/swarm-client.jar", "-disableSslVerification", "-master", "http://jenkins:8080"]
