package io.zeebe;

import java.util.Map;
import java.util.Scanner;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.clients.WorkflowClient;
import io.zeebe.client.api.events.DeploymentEvent;
import io.zeebe.client.api.events.WorkflowInstanceEvent;
import io.zeebe.client.api.subscription.JobWorker;
import io.zeebe.client.api.subscription.TopicSubscription;

public class Application
{

    public static void main(String[] args)
    {
        final String contactPoint = args.length >= 1 ? args[0] : "127.0.0.1:51015";

        System.out.println("Connecting to broker: " + contactPoint);

        final ZeebeClient client = ZeebeClient.newClientBuilder()
            .brokerContactPoint(contactPoint)
            .build();

        System.out.println("Connected to broker: " + contactPoint);

        final WorkflowClient workflowClient = client.topicClient().workflowClient();

        final DeploymentEvent deployment = workflowClient.newDeployCommand()
            .addResourceFromClasspath("order-process.bpmn")
            .send()
            .join();

        final int version = deployment.getDeployedWorkflows().get(0).getVersion();
        System.out.println("Workflow deployed. Version: " + version);

        final WorkflowInstanceEvent wfInstance = workflowClient.newCreateInstanceCommand()
            .bpmnProcessId("order-process")
            .latestVersion()
            .payload("{ \"orderId\": 31243, \"orderItems\": [435, 182, 376] }")
            .send()
            .join();

        final long workflowInstanceKey = wfInstance.getWorkflowInstanceKey();

        System.out.println("Workflow instance created. Key: " + workflowInstanceKey);

        final JobWorker jobWorker = client.topicClient().jobClient()
            .newWorker()
            .jobType("payment-service")
            .handler((jobClient, job) ->
            {
                final Map<String, Object> headers = job.getCustomHeaders();
                final String method = (String) headers.get("method");

                final String orderId = job.getPayload();

                System.out.println("Process order: " + orderId);
                System.out.println("Collect money using payment method: " + method);

                // ...

                jobClient.newCompleteCommand(job)
                    .payload("{ \"totalPrice\": 46.50 }")
                    .send()
                    .join();
            })
            .open();

        final TopicSubscription topicSubscription = client.topicClient().newSubscription()
            .name("app-monitoring")
            .jobEventHandler(e -> System.out.println(e.toJson()))
            .workflowInstanceEventHandler(e -> System.out.println(e.toJson()))
            .startAtHeadOfTopic()
            .open();

        waitUntilClose();

        jobWorker.close();
        topicSubscription.close();

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
