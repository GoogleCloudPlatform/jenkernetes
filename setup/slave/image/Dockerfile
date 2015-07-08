FROM google/cloud-sdk
ADD swarm-client.jar /lib/
ENTRYPOINT ["java", "-jar", "/lib/swarm-client.jar", "-disableSslVerification", "-master", "http://jenkins:8080"]
