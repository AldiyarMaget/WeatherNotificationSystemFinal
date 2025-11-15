package org.example.sensor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.core.WeatherData;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GoogleWeatherCurrentSensor implements Sensor {
    @Override
    public List<WeatherData> read() throws IOException {
        File file = new File("data/googleweathercurrent.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(file);

        return WeatherData.builder()
                .temperature(root.path("temperature").asDouble(0.0))
                .feelsLike(root.path("feelsLike").asDouble(0.0))
                .minTemperature(root.path("tempMin").asDouble(0.0))
                .maxTemperature(root.path("tempMax").asDouble(0.0))
                .humidity(root.path("humidity").asInt(0))
                .cloudCover(root.path("cloudCover").asInt(0))
                .windSpeed(root.path("windSpeed").asDouble(0.0))
                .windDirection(root.path("windDirection").asText(""))
                .description(root.path("description").asText(""))
                .date(root.path("date").asText(null))
                .precipitationPercent(root.path("precipitationPercent").asInt(0))
                .sunrise(root.path("sunrise").asText(null))
                .sunset(root.path("sunset").asText(null))
                .build();
    }
}
