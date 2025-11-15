package org.example.sensor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.core.WeatherData;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GoogleWeatherHourSensor implements Sensor {
    @Override
    public List<WeatherData> read() throws IOException {
        File file = new File("data/googleweatherhour.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(file);

        // Если массив часов, берём первый элемент
        JsonNode firstHour = root.isArray() ? root.get(0) : root;

        return WeatherData.builder()
                .temperature(firstHour.path("temperature").asDouble(0.0))
                .feelsLike(firstHour.path("feelsLike").asDouble(0.0))
                .humidity(firstHour.path("humidity").asInt(0))
                .cloudCover(firstHour.path("cloudCover").asInt(0))
                .windSpeed(firstHour.path("windSpeed").asDouble(0.0))
                .windDirection(firstHour.path("windDirection").asText(""))
                .description(firstHour.path("description").asText(""))
                .date(firstHour.path("date").asText(null))
                .precipitationPercent(firstHour.path("precipitationPercent").asInt(0))
                .build();
    }
}
