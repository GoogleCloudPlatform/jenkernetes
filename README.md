## Jenkernetes

This repository contains two tools for running a Jenkins server on Kubernetes

* [kubernetes-swarm-client](client/README.md): A client jar which connects a container running in a Kubernetes cluster with a Jenkins server running in the same cluster
* [kubernetes-workflow-steps](plugin/README.md): A Jenkins plugin which allows [workflow-scripts](https://github.com/jenkinsci/workflow-plugin) to provision Kubernetes resources (e.g. pods, replication controllers).

Combined, these two tools allow users to specify both the workflow and environment for a build in a workflow-script that can be stored along side the code in your SCM.


## Setup

Take a look at the README files for the [swarm-client](client/README.md) or the [workflow-steps-plugin](plugin/README.md) for more in depth instructions on setting up and using these tools.

## Contributing changes

* See [CONTRIBUTING.md](CONTRIBUTING.md)

## Licensing

* See [LICENSE](LICENSE)
