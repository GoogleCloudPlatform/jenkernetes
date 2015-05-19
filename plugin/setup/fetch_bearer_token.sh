#!/bin/bash

gcloud compute copy-files root@k8s-$1-node-1:/var/lib/kubelet/kubernetes_auth kubernetes_auth
echo -e "{\"metadata\": {\"name\": \"kubelet-bearer-token\"},\"apiVersion\": \"v1beta3\",\"kind\": \"Secret\",\"data\": {\"kubelet-bearer-token.json\": \"`base64 --wrap=0 kubernetes_auth`\"}}" > bearer_token.json
kubectl create -f bearer_token.json
rm kubernetes_auth
