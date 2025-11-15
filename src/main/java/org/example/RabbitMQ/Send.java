package org.example.RabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Send {

    private static final String QUEUE_REQUEST = "weather_request";

    public static void sendRequest(String jsonRequest) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_REQUEST, false, false, false, null);

            channel.basicPublish(
                    "",
                    QUEUE_REQUEST,
                    null,
                    jsonRequest.getBytes()
            );

            System.out.println("[MAIN] Sent request: " + jsonRequest);
        }
    }
}
