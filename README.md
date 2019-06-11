# Zeebe - Get Started Java Client

This repository contains the source code of the Zeebe Get-Started Java client tutorial.

You can find the tutorial in the [Zeebe documentation](http://docs.zeebe.io/java-client/get-started).

* [Web Site](https://zeebe.io)
* [Documentation](https://docs.zeebe.io)
* [Issue Tracker](https://github.com/zeebe-io/zeebe/issues)
* [Slack Channel](https://zeebe-slackin.herokuapp.com/)
* [User Forum](https://forum.zeebe.io)
* [Contribution Guidelines](/CONTRIBUTING.md)

## Run with Maven

Build the JAR file with Maven

`mvn clean package`

And execute it with Java

`java -jar target/zeebe-get-started-java-client-0.1.0-jar-with-dependencies.jar`

## docker-compose

Make sure to build the worker by running `mvn clean package`.

Then start the broker:

```
docker-compose up -d broker
```

and start the worker

```
docker-compose up worker
```

You should see output like this:

```
$ docker-compose up worker
Creating zeebe-get-started-java-client_worker_1_7b7ff1087d87 ... done
Attaching to zeebe-get-started-java-client_worker_1_1b0b3eca78ad
worker_1_1b0b3eca78ad | Connecting to broker: zeebe-broker:26500
worker_1_1b0b3eca78ad | Connected to broker: zeebe-broker:26500
worker_1_1b0b3eca78ad | Workflow deployed. Version: 1
worker_1_1b0b3eca78ad | Workflow instance created. Key: 6
worker_1_1b0b3eca78ad | Closed.
zeebe-get-started-java-client_worker_1_1b0b3eca78ad exited with code 0
```

## Kubernetes

Bare bones example for kubernetes is provided. Because of the wide variations in providers, we cannot provide examples for all. This should work on GKE 1.12 and be adaptable for your installation.

### Prerequisites

- Zeebe gateway(s) in front of zeebe broker(s) running
- A service called `zeebe` exposing a port called `gateway` in the same namespace (set in `kustomization.yaml` and defaulting to `demo-zeebe`) pointing to said gateway(s)
- skaffold, kustomize, docker, GNU Make and gcloud installed
- a gcr.io image repository to push the finished image

### Running

```
make skaffold
```

1. Make will populate skaffold.yaml and kustomization.yaml with your GCP project ID
1. A maven docker image will be downloaded to build the target jar
1. Skaffold will build the Dockerfile and push it to your gcr
1. Skaffold will call `kustomize build`
1. Skaffold will rewrite the image tags in the manifests to match the one it just pushed
1. The worker will start, looking for the service `zeebe` in the same namespace.

The service must be created before the demo pod starts.

You do not need to set the environment variables if your service is configured correctly. They are [provided by kubernetes](https://kubernetes.io/docs/concepts/services-networking/service/#environment-variables).

### Output

If everything is successful, you should have something like this (zeebe-0 is a broker/gateway configured seperately)
```
➜ kubectl get all -n demo-zeebe
NAME                                READY   STATUS             RESTARTS   AGE
pod/zeebe-0                         1/1     Running            0          47m
pod/zeebe-client-58cd7d45dc-cwx5x   0/1     CrashLoopBackOff   6          12m

NAME            TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)                                             AGE
service/zeebe   ClusterIP   10.11.241.252   <none>        26500/TCP,26501/TCP,26502/TCP,26503/TCP,26504/TCP   132m

NAME                           DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/zeebe-client   1         1         1            0           12m

NAME                                      DESIRED   CURRENT   READY   AGE
replicaset.apps/zeebe-client-58cd7d45dc   1         1         0       12m

NAME                     DESIRED   CURRENT   AGE
statefulset.apps/zeebe   1         1         132m

➜ kubectl logs -n demo-zeebe zeebe-client-58cd7d45dc-cwx5x 
Connecting to broker: 10.11.241.252:26500
Connected to broker: 10.11.241.252:26500
Workflow deployed. Version: 16
Workflow instance created. Key: 2251799813685412
Closed.
```

## Code of Conduct

This project adheres to the Contributor Covenant [Code of
Conduct](/CODE_OF_CONDUCT.md). By participating, you are expected to uphold
this code. Please report unacceptable behavior to code-of-conduct@zeebe.io.

## License

Most Zeebe source files are made available under the [Apache License, Version
2.0](/LICENSE) except for the [broker-core][] component. The [broker-core][]
source files are made available under the terms of the [GNU Affero General
Public License (GNU AGPLv3)][agpl]. See individual source files for
details.

[broker-core]: https://github.com/zeebe-io/zeebe/tree/master/broker-core
[agpl]: https://github.com/zeebe-io/zeebe/blob/master/GNU-AGPL-3.0
