FROM ubuntu:14.04
ADD swarm-client.jar /lib/
RUN apt-get -y update && apt-get -y install git python-pip openjdk-7-jre  openjdk-7-jdk wget python-dev python3-dev maven && apt-get clean
RUN pip install tox
ENV JAVA_HOME /usr/lib/jvm/java-7-openjdk-amd64
ENTRYPOINT ["java", "-jar", "/lib/swarm-client.jar", "-disableSslVerification", "-master", "http://jenkins:8080", "-labels", "python", "-executors", "1"]
