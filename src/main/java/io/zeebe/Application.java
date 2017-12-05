package io.zeebe;

import java.time.Duration;
import java.util.*;

import io.zeebe.client.ClientProperties;
import io.zeebe.client.ZeebeClient;
import io.zeebe.client.event.*;
import io.zeebe.client.task.TaskSubscription;

public class Application
{
    private static final String TOPIC = "default-topic";

    public static void main(String[] args)
    {
        final String contactPoint = args.length >= 1 ? args[0] : "127.0.0.1:51015";
        final Properties clientProperties = new Properties();
        // change the contact point if needed
        clientProperties.put(ClientProperties.BROKER_CONTACTPOINT, contactPoint);

        System.out.println("Connecting to broker: " + contactPoint);

        final ZeebeClient client = ZeebeClient.create(clientProperties);

        System.out.println("Connected to broker: " + contactPoint);

        final DeploymentEvent deployment = client.workflows().deploy(TOPIC)
            .addResourceFromClasspath("order-process.bpmn")
            .execute();

        final int version = deployment.getDeployedWorkflows().get(0).getVersion();
        System.out.println("Workflow deployed. Version: " + version);

        final WorkflowInstanceEvent wfInstance = client.workflows().create(TOPIC)
            .bpmnProcessId("order-process")
            .latestVersion()
            .payload("{ \"orderId\": 31243, \"orderItems\": [435, 182, 376] }")
            .execute();

        final long workflowInstanceKey = wfInstance.getWorkflowInstanceKey();

        System.out.println("Workflow instance created. Key: " + workflowInstanceKey);

        final TaskSubscription taskSubscription = client.tasks().newTaskSubscription(TOPIC)
            .taskType("payment-service")
            .lockOwner("sample-app")
            .lockTime(Duration.ofMinutes(5))
            .handler((tasksClient, task) ->
            {
                final Map<String, Object> headers = task.getCustomHeaders();
                final String method = (String) headers.get("method");

                final String orderId = task.getPayload();

                System.out.println("Process order: " + orderId);
                System.out.println("Collect money using payment method: " + method);

                // ...

                tasksClient
                        .complete(task)
                        .payload("{ \"totalPrice\": 46.50 }")
                        .execute();
            })
            .open();

        final TopicSubscription topicSubscription = client.topics().newSubscription(TOPIC)
            .name("app-monitoring")
            .startAtHeadOfTopic()
            .workflowInstanceEventHandler(event ->
            {
                System.out.println("> " + event);
            })
            .open();

        waitUntilClose();

        taskSubscription.close();
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
