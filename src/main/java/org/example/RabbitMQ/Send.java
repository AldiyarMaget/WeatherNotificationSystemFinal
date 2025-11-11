package org.example.RabbitMQ;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
//в readme 1 заметка
public class Send {
    private final static String QUEUE_NAME = "hello";
    public static void main(String[] argv) throws Exception {
      ConnectionFactory factory = new ConnectionFactory();
      try (Connection connection = factory.newConnection();
           Channel channel = connection.createChannel()){
          channel.queueDeclare(QUEUE_NAME, false, false, false, null);
          String message = "Gigga Nigga";
          channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
          System.out.println(" [x] Sent '" + message + "'");
      }
    }
}