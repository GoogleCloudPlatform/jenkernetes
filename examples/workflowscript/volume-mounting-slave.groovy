def pod = [
	metadata: [
		name: "my-cloud-sdk-slave"
	],
	spec: [
		containers:[
			[
				name: "jenkins-slave",
				image: "google/cloud-sdk",
				command: [
					"java",
					"-jar", "/mnt/swarm-client/swarm-client.jar",
					"-master", "http://jenkins:8080",
					"-name", "my-cloud-sdk-slave",
					"-labels", "image=google/cloud-sdk"
				],
				volumeMounts: [
					[
						name: "swarm-client",
						mountPath: "/mnt/swarm-client"
					]

				]
			]
		],
		volumes: [
			[
				name: "swarm-client",
				GCEPersistentDisk: [
					pdName: "swarm-client",
					fsType: "ext4",
					readOnly: true
				]
			]
		],
		dnsPolicy: "Default",
		restartPolicy: "Never"
	]
]
//Read-Write requests to the Kubernetes API Endpoint can only be made from master
print kube_create(resource: "pods", payload: pod)
def thisnode
node("my-cloud-sdk-slave") {
	// Code in here will run on the kubernetes pod you just created
	// Read-only requests can be made from slave
	thisnode = kube_get(resource: "pods", name: env.HOSTNAME)
	print thisnode
}
//this code runs on master again
//Node that all responses returned from the kube_* commands are valid groovy maps.
print kube_delete(resource: "pods", name: thisnode["metadata"]["name"])
