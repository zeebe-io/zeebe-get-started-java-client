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
Starting zeebe-get-started-java-client_worker_1 ... done
Attaching to zeebe-get-started-java-client_worker_1
worker_1  | Connecting to broker: zeebe-broker:26500
worker_1  | Connected to broker: zeebe-broker:26500
worker_1  | Workflow deployed. Version: 1
worker_1  | Workflow instance created. Key: 2251799813685261
worker_1  | Closed.
zeebe-get-started-java-client_worker_1 exited with code 0

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
