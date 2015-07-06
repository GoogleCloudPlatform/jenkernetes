FROM google/cloud-sdk
ADD kubernetes-swarm-client-jar-with-dependencies.jar /lib/
ENTRYPOINT ["java", "-jar", "/lib/swarm-client.jar", "-disableSslVerification", "-master", "http://jenkins:8080"]
