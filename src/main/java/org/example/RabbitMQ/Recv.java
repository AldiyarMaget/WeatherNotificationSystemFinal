package org.example.RabbitMQ;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Recv {
    private static final String QUEUE_NAME = "weather";
    private static final ObjectMapper mapper = new ObjectMapper();

    private static JsonNode openWeather;
    private static JsonNode weatherAPI;
    private static JsonNode googleWeather;

    private static int counter = 0;

    public static void startListener() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        System.out.println("[*] Waiting for messages...");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            JsonNode json = mapper.readTree(message);

            String filePath = null;

            switch (counter) {
                case 0 -> {
                    openWeather = json;
                    filePath = "data/openweather.json";
                    System.out.println("OpenWeather received");
                }
                case 1 -> {
                    weatherAPI = json;
                    filePath = "data/weatherapi.json";
                    System.out.println("WeatherAPI received");
                }
                case 2 -> {
                    googleWeather = json;
                    filePath = "data/googleweather.json";
                    System.out.println("GoogleWeather received");
                }
            }

            counter = (counter + 1) % 3;

            // сохраняем JSON в файл, перезаписываем
            if (filePath != null) saveJsonToFile(json, filePath);

            // выводим блок только если все три объекта получены
            if (openWeather != null && weatherAPI != null && googleWeather != null) {
                System.out.println("\n=== ACTUAL WEATHER ===");
                System.out.println(openWeather());
                System.out.println(weatherAPI());
                System.out.println(googleWeather());
                System.out.println("====================\n");

                // сброс для получениях следущих данных
                openWeather = null;
                weatherAPI = null;
                googleWeather = null;
            }
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
    }

    private static void saveJsonToFile(JsonNode json, String path) {
        try {
            File folder = new File("data");
            if (!folder.exists()) folder.mkdirs(); // создаём папку, если нет
            FileWriter writer = new FileWriter(path);
            writer.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String parseTime(JsonNode node) {
        if (node.isTextual()) return node.asText();
        long ts = node.asLong() * 1000L;
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Almaty"));
        return sdf.format(new Date(ts));
    }

    private static String formatWeather(JsonNode json, String apiName) {
        if (json == null) return apiName + ": нет данных";
        String name = json.path("name").asText("-");
        String country = json.path("sys").path("country").asText("-");
        double temp = json.path("main").path("temp").asDouble(Double.NaN);
        double feels = json.path("main").path("feels_like").asDouble(Double.NaN);
        int humidity = json.path("main").path("humidity").asInt(-1);
        String mainW = json.path("weather").get(0).path("main").asText("-");
        String desc = json.path("weather").get(0).path("description").asText("-");
        String sunrise = parseTime(json.path("sys").path("sunrise"));
        String sunset = parseTime(json.path("sys").path("sunset"));

        return "\n--- " + apiName + " ---\n" +
                "Город: " + name + " (" + country + ")\n" +
                "Температура: " + temp + "°C (ощущается как " + feels + "°C)\n" +
                "Влажность: " + humidity + "%\n" +
                "Погода: " + mainW + " (" + desc + ")\n" +
                "Восход: " + sunrise + "\n" +
                "Закат: " + sunset;
    }

    public static JsonNode getOpenWeatherJson() { return openWeather; }
    public static JsonNode getWeatherAPIJson() { return weatherAPI; }
    public static JsonNode getGoogleWeatherJson() { return googleWeather; }

    public static String openWeather() { return formatWeather(openWeather, "OpenWeather"); }
    public static String weatherAPI() { return formatWeather(weatherAPI, "WeatherAPI"); }
    public static String googleWeather() { return formatWeather(googleWeather, "Google Weather"); }

}
