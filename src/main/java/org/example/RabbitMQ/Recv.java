package org.example.RabbitMQ;

import com.rabbitmq.client.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class Recv {

    private final static String QUEUE_NAME = "weather_response";

    public static void startListening() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println("[*] Waiting for messages...");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("[x] Received raw JSON:\n" + message);

            // Пример парсинга
            JSONObject json = new JSONObject(message);
            System.out.println("[x] Source: " + json.getString("Name"));
            JSONObject coord = json.getJSONObject("Coord");
            System.out.println("[x] Coordinates: " + coord.getDouble("lat") + ", " + coord.getDouble("lon"));

            JSONArray days = json.getJSONArray("Days");
            for (int i = 0; i < days.length(); i++) {
                JSONObject day = days.getJSONObject(i);
                System.out.println("Date: " + day.getString("Date"));
                JSONObject main = day.getJSONObject("Main");
                System.out.println("Temp: " + main.getDouble("temp") +
                        ", FeelsLike: " + main.getDouble("feels_like") +
                        ", Min: " + main.getDouble("temp_min") +
                        ", Max: " + main.getDouble("temp_max"));

                JSONArray weatherArr = day.getJSONArray("Weather");
                for (int j = 0; j < weatherArr.length(); j++) {
                    JSONObject w = weatherArr.getJSONObject(j);
                    System.out.println("Weather: " + w.getString("main") +
                            ", Description: " + w.getString("description"));
                }
                System.out.println("---");
            }
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });

        System.out.println("Listening. Press Ctrl+C to exit.");
        Thread.currentThread().join(); // блокируем основной поток
    }
}
