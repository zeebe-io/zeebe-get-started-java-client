package io.zeebe;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.response.DeploymentEvent;
import io.zeebe.client.api.response.WorkflowInstanceEvent;
import io.zeebe.client.api.worker.JobWorker;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Example application that connects to a cluster on Camunda Cloud, or a locally deployed cluster.
 *
 * <p>When connecting to a cluster in Camunda Cloud, this application assumes that the following
 * environment variables are set:
 *
 * <ul>
 *   <li>ZEEBE_ADDRESS
 *   <li>ZEEBE_CLIENT_ID (implicitly required by {@code ZeebeClient} if authorization is enabled)
 *   <li>ZEEBE_CLIENT_SECRET (implicitly required by {@code ZeebeClient} if authorization is enabled)
 *   <li>ZEEBE_AUTHORIZATION_SERVER_URL (implicitly required by {@code ZeebeClient} if authorization is enabled)
 * </ul>
 *
 * <p><strong>Hint:</strong> When you create client credentials in Camunda Cloud you have the option
 * to download a file with above lines filled out for you.
 *
 * <p>When connecting to a local cluster, you can specifiy that address either at the command line
 * or by setting {@code ZEEBE_ADDRESS}. This application also assumes that auhentication is disabled
 * for a locally deployed cluster
 */
public class Application {

  public static void main(String[] args) {
    final String defaultAddress = "127.0.0.1:26500";
    final String addressFromCommandLine = args.length >= 1 ? args[0] : null;
    final String addressFromEnvironmentVariable = System.getenv("ZEEBE_ADDRESS");

    final String gatewayAddress =
        addressFromCommandLine != null
            ? addressFromCommandLine
            : addressFromEnvironmentVariable != null
                ? addressFromEnvironmentVariable
                : defaultAddress;

    System.out.println("Connecting to gateway: " + gatewayAddress);

    final ZeebeClient client = createZeebeClient(gatewayAddress);

    System.out.println("Connected to gateway: " + gatewayAddress);

    final DeploymentEvent deployment =
        client.newDeployCommand().addResourceFromClasspath("order-process.bpmn").send().join();

    final int version = deployment.getWorkflows().get(0).getVersion();
    System.out.println("Workflow deployed. Version: " + version);

    final Map<String, Object> data = new HashMap<>();
    data.put("orderId", 31243);
    data.put("orderItems", Arrays.asList(435, 182, 376));

    final WorkflowInstanceEvent wfInstance =
        client
            .newCreateInstanceCommand()
            .bpmnProcessId("order-process")
            .latestVersion()
            .variables(data)
            .send()
            .join();

    final long workflowInstanceKey = wfInstance.getWorkflowInstanceKey();

    System.out.println("Workflow instance created. Key: " + workflowInstanceKey);

    final JobWorker jobWorker =
        client
            .newWorker()
            .jobType("payment-service")
            .handler(
                (jobClient, job) -> {
                  final Map<String, Object> variables = job.getVariablesAsMap();

                  System.out.println("Process order: " + variables.get("orderId"));
                  double price = 46.50;
                  System.out.println("Collect money: $" + price);

                  // ...

                  final Map<String, Object> result = new HashMap<>();
                  result.put("totalPrice", price);

                  jobClient.newCompleteCommand(job.getKey()).variables(result).send().join();
                })
            .fetchVariables("orderId")
            .open();

    waitUntilClose();

    jobWorker.close();

    client.close();
    System.out.println("Closed.");
  }

  private static ZeebeClient createZeebeClient(String gatewayAddress) {
    if (gatewayAddress.contains("zeebe.camunda.io")) {
      /* Connect to Camunda Cloud Cluster, assumes that credentials are set in environment variables.
       * See JavaDoc on class level for details
       */
      return ZeebeClient.newClientBuilder().gatewayAddress(gatewayAddress).build();
    } else {
      // connect to local deployment; assumes that authentication is disabled
      return ZeebeClient.newClientBuilder().gatewayAddress(gatewayAddress).usePlaintext().build();
    }
  }

  private static void waitUntilClose() {
    try (Scanner scanner = new Scanner(System.in)) {
      while (scanner.hasNextLine()) {
        final String nextLine = scanner.nextLine();
        if (nextLine.contains("close")) {
          return;
        }
      }
    }
  }
}
