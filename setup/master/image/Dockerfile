FROM jenkins:latest
ADD plugins.txt /usr/share/jenkins/
USER root
RUN plugins.sh /usr/share/jenkins/plugins.txt
ADD kubernetes-workflow-steps.hpi /usr/share/jenkins/ref/plugins/
