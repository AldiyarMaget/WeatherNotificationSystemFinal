package org.example.sensor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.RabbitMQ.Recv;
import org.example.RabbitMQ.Send;
import org.example.core.WeatherData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class GoogleWeatherWeeklySensor implements Sensor {
    @Override
    public List<WeatherData> read(String city) throws IOException {
        try {
            Send.sendRequest(city,"week");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            Recv.startListening();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        File file = new File("data/googleweatherweekly.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(file).path("data");

        List<WeatherData> list = new ArrayList<>();
        if (root.isArray()) {
            for (JsonNode node : root) {
                list.add(WeatherData.builder()
                        .temperature(node.path("tempMax").asDouble(0.0))
                        .feelsLike(node.path("feelsLikeMax").asDouble(0.0))
                        .minTemperature(node.path("tempMin").asDouble(0.0))
                        .maxTemperature(node.path("tempMax").asDouble(0.0))
                        .humidity(node.path("humidity").asDouble(0.0))
                        .cloudCover(node.path("cloudCover").asInt(0))
                        .windSpeed(node.path("windSpeed").asDouble(0.0))
                        .windDirection(node.path("windDirection").asText(""))
                        .description(node.path("description").asText(""))
                        .mainInfo(node.path("description").asText(""))
                        .sunrise(node.path("sunrise").asText(""))
                        .sunset(node.path("sunset").asText(""))
                        .date(node.path("date").asText(""))
                        .precipitationPercent(node.path("precipitationPercent").asInt(0))
                        .build());
            }
        }
        return list;
    }
}
