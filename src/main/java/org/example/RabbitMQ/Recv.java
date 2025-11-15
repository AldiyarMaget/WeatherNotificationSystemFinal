package org.example.RabbitMQ;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;

public class Recv {

    private static final String QUEUE_RESPONSE = "weather_response";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void startListening() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_RESPONSE, false, false, false, null);

        System.out.println("[*] Waiting for messages...");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String json = new String(delivery.getBody(), StandardCharsets.UTF_8);

            System.out.println("[x] Received JSON:");
            System.out.println(json);

            try {
                JsonNode root = mapper.readTree(json);
                String type = root.path("type").asText().trim().toLowerCase();

                String filename = switch (type) {
                    case "current" -> "googleweathercurrent.json";
                    case "3 hour", "hour" -> "googleweatherhour.json";
                    case "today" -> "googleweathertoday.json";
                    case "tomorrow" -> "googleweathertomorrow.json";
                    case "week" -> "googleweatherweekly.json";
                    default -> "googleweather_unknown.json";
                };

                saveJsonToFile(json, filename);
            } catch (Exception e) {
                System.out.println("[ERROR] Failed to process JSON:");
                e.printStackTrace();
            }
        };

        channel.basicConsume(QUEUE_RESPONSE, true, deliverCallback, consumerTag -> {});
    }

    private static void saveJsonToFile(String json, String filename) {
        try {
            File folder = new File("data");
            if (!folder.exists()) folder.mkdirs();

            File file = new File(folder, filename);

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(json);
            }

            System.out.println("[+] Saved to: " + file.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to save file:");
            e.printStackTrace();
        }
    }
}
