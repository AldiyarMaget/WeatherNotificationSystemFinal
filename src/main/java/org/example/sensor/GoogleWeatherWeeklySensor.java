package org.example.sensor;

import org.example.core.WeatherData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GoogleWeatherWeeklySensor implements Sensor {
    @Override
    public List<WeatherData> read() throws IOException {
        File file = new File("data/googleweatherweekly.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(file);

        List<WeatherData> list = new ArrayList<>();
        if (root.isArray()) {
            for (JsonNode node : root) {
                list.add(WeatherData.builder()
                        .city(node.path("city").asText(""))
                        .temperature(node.path("tempMax").asDouble(0))
                        .feelsLike(node.path("feelsLikeMax").asDouble(0))
                        .minTemperature(node.path("tempMin").asDouble(0))
                        .maxTemperature(node.path("tempMax").asDouble(0))
                        .humidity(node.path("humidity").asDouble(0))
                        .sunrise(node.path("sunrise").asText(""))
                        .sunset(node.path("sunset").asText(""))
                        .description(node.path("description").asText(""))
                        .mainInfo(node.path("description").asText(""))
                        .build());
            }
        }
        return list;
    }
}
