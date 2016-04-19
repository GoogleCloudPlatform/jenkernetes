FROM gcr.io/google_appengine/nodejs
ADD swarm-client.jar /lib/
RUN apt-get -y update && apt-get -y install git openjdk-7-jre  openjdk-7-jdk wget libpng-dev && apt-get clean
ENTRYPOINT ["java", "-jar", "/lib/swarm-client.jar", "-disableSslVerification", "-master", "http://jenkins:8080", "-labels", "nodejs-slave", "-executors", "1"]
