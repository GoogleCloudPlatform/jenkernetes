FROM ubuntu:14.04
RUN apt-get update -q && apt-get install -y software-properties-common && apt-get clean
RUN add-apt-repository -y ppa:fkrull/deadsnakes
RUN apt-get -y update && \
    apt-get -y install \
        git wget \
        python-pip python-dev python3-dev python3.5 python3.5-dev \
        openjdk-7-jre openjdk-7-jdk maven && \
    apt-get clean
ADD swarm-client.jar /lib/
RUN pip install tox
ENV JAVA_HOME /usr/lib/jvm/java-7-openjdk-amd64
ENTRYPOINT ["java", "-jar", "/lib/swarm-client.jar", "-disableSslVerification", "-master", "http://jenkins:8080", "-labels", "python", "-executors", "1"]
