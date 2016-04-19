FROM ruby:2.2.3
ADD swarm-client.jar /lib/
RUN apt-get -y update && apt-get -y install git openjdk-7-jre  openjdk-7-jdk wget && apt-get clean
ENV JAVA_HOME /usr/lib/jvm/java-7-openjdk-amd64
ENTRYPOINT ["java", "-jar", "/lib/swarm-client.jar", "-disableSslVerification", "-master", "http://jenkins:8080", "-labels", "ruby-slave", "-executors", "1"]
