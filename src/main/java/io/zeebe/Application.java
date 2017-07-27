package io.zeebe;

import java.time.Duration;
import java.util.Properties;
import java.util.Scanner;

import io.zeebe.client.ClientProperties;
import io.zeebe.client.ZeebeClient;
import io.zeebe.client.event.TopicSubscription;
import io.zeebe.client.task.TaskSubscription;
import io.zeebe.client.workflow.cmd.DeploymentResult;
import io.zeebe.client.workflow.cmd.WorkflowInstance;

public class Application
{
    private static final String DEFAULT_TOPIC = "default-topic";
    private static final int DEFAULT_PARTITION_ID = 0;

    public static void main(String[] args)
    {
        final Properties clientProperties = new Properties();
        clientProperties.put(ClientProperties.BROKER_CONTACTPOINT, "127.0.0.1:51015");

        final ZeebeClient client = ZeebeClient.create(clientProperties);

        client.connect();
        System.out.println("Connected.");

        final DeploymentResult deploymentResult = client.workflowTopic(DEFAULT_TOPIC, DEFAULT_PARTITION_ID).deploy()
            .resourceFromClasspath("order-process.bpmn")
            .execute();

        if (deploymentResult.isDeployed())
        {
            System.out.println("Workflow deployed: " + deploymentResult);
        }
        else
        {
            System.out.println("Failed to deploy workflow: " + deploymentResult.getErrorMessage());
        }

        final WorkflowInstance workflowInstance = client.workflowTopic(DEFAULT_TOPIC, DEFAULT_PARTITION_ID).create()
            .bpmnProcessId("order-process")
            .latestVersion()
            .payload("{ \"orderId\": 31243, \"orderStatus\": \"NEW\", \"orderItems\": [435, 182, 376] }")
            .execute();

        System.out.println("Workflow instance created: " + workflowInstance);

        final TopicSubscription topicSubscription = client.topic(DEFAULT_TOPIC, DEFAULT_PARTITION_ID).newSubscription()
            .name("app-monitoring")
            .startAtHeadOfTopic()
            .workflowInstanceEventHandler((metadata, event ) ->
            {
                System.out.println("> " + event);
            })
            .open();

        final TaskSubscription taskSubscription = client.taskTopic(DEFAULT_TOPIC, DEFAULT_PARTITION_ID).newTaskSubscription()
            .taskType("reserveOrderItems")
            .lockOwner("stocker")
            .lockTime(Duration.ofMinutes(5))
            .handler(task ->
            {
                final String reservationTime = (String) task.getHeaders().get("reservationTime");

                final String orderItems = task.getPayload();

                System.out.println("Reserved " + orderItems + " for " + reservationTime);

                // ...

                task.complete("{ \"orderStatus\": \"RESERVED\" }");
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
