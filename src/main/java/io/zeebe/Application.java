package io.zeebe;

import java.util.*;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.clients.WorkflowClient;
import io.zeebe.client.api.events.DeploymentEvent;
import io.zeebe.client.api.events.WorkflowInstanceEvent;
import io.zeebe.client.api.subscription.JobWorker;

public class Application
{

    public static void main(String[] args)
    {
        final String contactPoint = args.length >= 1 ? args[0] : "127.0.0.1:26500";

        System.out.println("Connecting to broker: " + contactPoint);

        final ZeebeClient client = ZeebeClient.newClientBuilder()
            .brokerContactPoint(contactPoint)
            .build();

        System.out.println("Connected to broker: " + contactPoint);

        final WorkflowClient workflowClient = client.workflowClient();

        final DeploymentEvent deployment = workflowClient.newDeployCommand()
            .addResourceFromClasspath("order-process.bpmn")
            .send()
            .join();

        final int version = deployment.getWorkflows().get(0).getVersion();
        System.out.println("Workflow deployed. Version: " + version);

        final Map<String, Object> data = new HashMap<>();
        data.put("orderId", 31243);
        data.put("orderItems", Arrays.asList(435, 182, 376));

        final WorkflowInstanceEvent wfInstance = workflowClient.newCreateInstanceCommand()
            .bpmnProcessId("order-process")
            .latestVersion()
            .payload(data)
            .send()
            .join();

        final long workflowInstanceKey = wfInstance.getWorkflowInstanceKey();

        System.out.println("Workflow instance created. Key: " + workflowInstanceKey);

        final JobWorker jobWorker = client.jobClient()
            .newWorker()
            .jobType("payment-service")
            .handler((jobClient, job) ->
            {
                final Map<String, Object> headers = job.getCustomHeaders();
                final String method = (String) headers.get("method");

                final Map<String, Object> payload = job.getPayloadAsMap();

                System.out.println("Process order: " + payload.get("orderId"));
                System.out.println("Collect money using payment method: " + method);

                // ...

                payload.put("totalPrice", 46.50);
                jobClient.newCompleteCommand(job.getKey())
                    .payload(payload)
                    .send()
                    .join();
            })
            .open();

        waitUntilClose();

        jobWorker.close();

        client.close();
        System.out.println("Closed.");
    }

    private static void waitUntilClose()
    {
        try (Scanner scanner = new Scanner(System.in))
        {
            while (scanner.hasNextLine())
            {
                final String nextLine = scanner.nextLine();
                if (nextLine.contains("close"))
                {
                    return;
                }
            }
        }
    }

}
