#!/bin/bash
gcloud compute disks create jenkins-config --type pd-ssd --size 10GB
gcloud compute instances create temp-writer --disk name=jenkins-config,device-name=jenkins-config \
	                    --metadata startup-script="sudo mkdir /mnt/jenkins-config && sudo /usr/share/google/safe_format_and_mount /dev/disk/by-id/google-jenkins-config /mnt/jenkins-config"
gcloud compute ssh temp-writer --command "sudo umount /mnt/jenkins-config"
gcloud compute instances detach-disk temp-writer --disk jenkins-config
gcloud compute instances delete temp-writer
