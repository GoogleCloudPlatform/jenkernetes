FROM google/cloud-sdk
ADD kubernetes-swarm-client-jar-with-dependencies.jar /lib/
ENTRYPOINT ["java", "-jar", "/lib/kubernetes-swarm-client-jar-with-dependencies.jar", "-disableSslVerification"]
