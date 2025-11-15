package org.example.sensor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.core.WeatherData;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class GoogleWeatherCurrentSensor implements Sensor {
    @Override
    public List<WeatherData> read() throws IOException {
        File file = new File("data/googleweathercurrent.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(file).path("data");

        return Collections.singletonList(WeatherData.builder()
                .temperature(root.path("temperature").asDouble(0.0))
                .feelsLike(root.path("feelsLike").asDouble(0.0))
                .humidity(root.path("humidity").asDouble(0.0))
                .cloudCover(root.path("cloudCover").asInt(0))
                .windSpeed(root.path("windSpeed").asDouble(0.0))
                .windDirection(root.path("windDirection").asText(""))
                .description(root.path("description").asText(""))
                .mainInfo(root.path("description").asText(""))
                .build());
    }
}
