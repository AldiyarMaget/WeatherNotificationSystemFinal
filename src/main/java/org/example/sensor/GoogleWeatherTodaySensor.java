package org.example.sensor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.core.WeatherData;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class GoogleWeatherTodaySensor implements Sensor {
    @Override
    public List<WeatherData> read() throws IOException {
        File file = new File("data/googleweathertoday.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(file).path("data");

        if (root.isArray() && root.size() > 0) {
            JsonNode todayNode = root.get(0);
            return Collections.singletonList(WeatherData.builder()
                    .temperature(todayNode.path("tempMax").asDouble(0.0))
                    .feelsLike(todayNode.path("feelsLikeMax").asDouble(0.0))
                    .minTemperature(todayNode.path("tempMin").asDouble(0.0))
                    .maxTemperature(todayNode.path("tempMax").asDouble(0.0))
                    .humidity(todayNode.path("humidity").asDouble(0.0))
                    .cloudCover(todayNode.path("cloudCover").asInt(0))
                    .windSpeed(todayNode.path("windSpeed").asDouble(0.0))
                    .windDirection(todayNode.path("windDirection").asText(""))
                    .description(todayNode.path("description").asText(""))
                    .mainInfo(todayNode.path("description").asText(""))
                    .sunrise(todayNode.path("sunrise").asText(""))
                    .sunset(todayNode.path("sunset").asText(""))
                    .date(todayNode.path("date").asText(""))
                    .precipitationPercent(todayNode.path("precipitationPercent").asInt(0))
                    .build());
        }
        return Collections.emptyList();
    }
}
