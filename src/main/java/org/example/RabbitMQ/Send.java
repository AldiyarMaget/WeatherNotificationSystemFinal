package org.example.RabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Send {
    private final static String QUEUE_NAME = "city";

    public void sendCity(String city) {
        ConnectionFactory factory = new ConnectionFactory();
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", QUEUE_NAME, null, city.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + city + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
