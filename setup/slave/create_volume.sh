#!/bin/bash
gcloud compute disks create swarm-client --type pd-ssd --size 10GB
gcloud compute instances create temp-writer --disk name=swarm-client,device-name=swarm-client \
	                    --metadata startup-script="sudo mkdir /mnt/swarm-client && sudo /usr/share/google/safe_format_and_mount /dev/disk/by-id/google-swarm-client /mnt/swarm-client"
gcloud compute copy-files ./image/swarm-client.jar root@temp-writer:/mnt/swarm-client/swarm-client.jar
gcloud compute ssh temp-writer --command "sudo umount /mnt/swarm-client"
gcloud compute instances detach-disk temp-writer --disk swarm-client
gcloud compute instances delete temp-writer
