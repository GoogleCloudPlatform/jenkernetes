FROM ubuntu:14.04

RUN apt-get update -q && apt-get install -y software-properties-common && apt-get clean
RUN add-apt-repository -y ppa:fkrull/deadsnakes
RUN apt-get -y update && \
    apt-get -y install \
        git wget curl \
        python-pip python-dev python3-dev python3.5 python3.5-dev python3.6 python3.6-dev \
        openjdk-7-jre openjdk-7-jdk maven \
        libffi-dev libssl-dev libxml2-dev \
        libxslt1-dev libpq-dev libmysqlclient-dev libcurl4-openssl-dev \
        libjpeg-dev zlib1g-dev libpng12-dev  python-pyaudio && \
    apt-get clean

RUN pip install tox

# Setup Cloud SDK
ENV CLOUD_SDK_VERSION 146.0.0
RUN wget https://dl.google.com/dl/cloudsdk/channels/rapid/downloads/google-cloud-sdk-$CLOUD_SDK_VERSION-linux-x86_64.tar.gz
RUN tar xzf google-cloud-sdk-$CLOUD_SDK_VERSION-linux-x86_64.tar.gz
RUN /google-cloud-sdk/install.sh
ENV PATH /google-cloud-sdk/bin:$PATH

# Setup Jenkins swarm client
ADD swarm-client.jar /lib/
ENV JAVA_HOME /usr/lib/jvm/java-7-openjdk-amd64
ENTRYPOINT ["java", "-jar", "/lib/swarm-client.jar", "-disableSslVerification", "-master", "http://jenkins:8080", "-labels", "python35", "-executors", "1"]
