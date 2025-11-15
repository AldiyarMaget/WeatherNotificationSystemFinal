package org.example.RabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

public class Send {

    private static final String QUEUE_REQUEST = "weather_request";

    public static void sendRequest(String city, String type) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_REQUEST, false, false, false, null);

            String jsonRequest = String.format("{\"city\":\"%s\",\"type\":\"%s\"}", city, type);
            channel.basicPublish("", QUEUE_REQUEST, null, jsonRequest.getBytes(StandardCharsets.UTF_8));

            System.out.println("[x] Sent request: " + jsonRequest);
        }
    }

}
