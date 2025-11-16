package org.example.sensor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.core.WeatherData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GoogleWeatherHourSensor implements Sensor {
    @Override
    public List<WeatherData> read(String city) throws IOException {
        File file = new File("data/googleweatherhour.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(file).path("data");

        List<WeatherData> list = new ArrayList<>();
        if (root.isArray()) {
            for (JsonNode node : root) {
                list.add(WeatherData.builder()
                        .temperature(node.path("temperature").asDouble(0.0))
                        .feelsLike(node.path("feelsLike").asDouble(0.0))
                        .humidity(node.path("humidity").asDouble(0.0))
                        .cloudCover(node.path("cloudCover").asInt(0))
                        .windSpeed(node.path("windSpeed").asDouble(0.0))
                        .windDirection(node.path("windDirection").asText(""))
                        .description(node.path("description").asText(""))
                        .mainInfo(node.path("description").asText(""))
                        .date(node.path("time").asText(""))
                        .precipitationPercent(node.path("precipitationPercent").asInt(0))
                        .build());
            }
        }
        return list;
    }
}
