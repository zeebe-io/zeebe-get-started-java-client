# Zeebe - Get Started Jave Client

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
Creating zeebegetstartedjavaclient_worker_1 ...
Creating zeebegetstartedjavaclient_worker_1 ... done
Attaching to zeebegetstartedjavaclient_worker_1
worker_1  | Connecting to broker: zeebe-broker:51015
worker_1  | Connected to broker: zeebe-broker:51015
worker_1  | Workflow deployed. Version: 1
worker_1  | Workflow instance created. Key: 4294983520
worker_1  | Process order: {"orderId":31243}
worker_1  | Collect money using payment method: VISA
worker_1  | > WorkflowInstanceEvent [state=CREATE_WORKFLOW_INSTANCE, workflowInstanceKey=-1, workflowKey=-1, bpmnProcessId=order-process, version=-1, activityId=null, payload={"orderId":31243,"orderItems":[435,182,376]}]
worker_1  | > WorkflowInstanceEvent [state=WORKFLOW_INSTANCE_CREATED, workflowInstanceKey=4294983520, workflowKey=4294978128, bpmnProcessId=order-process, version=1, activityId=, payload={"orderId":31243,"orderItems":[435,182,376]}]
worker_1  | > WorkflowInstanceEvent [state=START_EVENT_OCCURRED, workflowInstanceKey=4294983520, workflowKey=4294978128, bpmnProcessId=order-process, version=1, activityId=order-placed, payload={"orderId":31243,"orderItems":[435,182,376]}]
worker_1  | > WorkflowInstanceEvent [state=SEQUENCE_FLOW_TAKEN, workflowInstanceKey=4294983520, workflowKey=4294978128, bpmnProcessId=order-process, version=1, activityId=SequenceFlow_18tqka5, payload={"orderId":31243,"orderItems":[435,182,376]}]
worker_1  | > WorkflowInstanceEvent [state=ACTIVITY_READY, workflowInstanceKey=4294983520, workflowKey=4294978128, bpmnProcessId=order-process, version=1, activityId=collect-money, payload={"orderId":31243,"orderItems":[435,182,376]}]
worker_1  | > WorkflowInstanceEvent [state=ACTIVITY_ACTIVATED, workflowInstanceKey=4294983520, workflowKey=4294978128, bpmnProcessId=order-process, version=1, activityId=collect-money, payload={"orderId":31243}]
worker_1  | > WorkflowInstanceEvent [state=ACTIVITY_COMPLETING, workflowInstanceKey=4294983520, workflowKey=4294978128, bpmnProcessId=order-process, version=1, activityId=collect-money, payload={"totalPrice":46.5}]
worker_1  | > WorkflowInstanceEvent [state=ACTIVITY_COMPLETED, workflowInstanceKey=4294983520, workflowKey=4294978128, bpmnProcessId=order-process, version=1, activityId=collect-money, payload={"orderId":31243,"orderItems":[435,182,376],"totalPrice":46.5}]
worker_1  | > WorkflowInstanceEvent [state=SEQUENCE_FLOW_TAKEN, workflowInstanceKey=4294983520, workflowKey=4294978128, bpmnProcessId=order-process, version=1, activityId=SequenceFlow_10zt7r3, payload={"orderId":31243,"orderItems":[435,182,376],"totalPrice":46.5}]
worker_1  | > WorkflowInstanceEvent [state=ACTIVITY_READY, workflowInstanceKey=4294983520, workflowKey=4294978128, bpmnProcessId=order-process, version=1, activityId=fetch-items, payload={"orderId":31243,"orderItems":[435,182,376],"totalPrice":46.5}]
worker_1  | > WorkflowInstanceEvent [state=ACTIVITY_ACTIVATED, workflowInstanceKey=4294983520, workflowKey=4294978128, bpmnProcessId=order-process, version=1, activityId=fetch-items, payload={"orderId":31243,"orderItems":[435,182,376],"totalPrice":46.5}]
worker_1  | Closed.
zeebegetstartedjavaclient_worker_1 exited with code 0
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
