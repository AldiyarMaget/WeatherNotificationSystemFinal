package org.example.sensor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.core.WeatherData;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GoogleWeatherTodaySensor implements Sensor {
    @Override
    public List<WeatherData> read() throws IOException {
        File file = new File("data/googleweathertoday.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(file);

        JsonNode first = root.get(0); // берем первый день
        return WeatherData.builder()
                .city(first.path("city").asText(""))
                .temperature(first.path("tempMax").asDouble(0))
                .feelsLike(first.path("feelsLikeMax").asDouble(0))
                .minTemperature(first.path("tempMin").asDouble(0))
                .maxTemperature(first.path("tempMax").asDouble(0))
                .humidity(first.path("humidity").asDouble(0))
                .sunrise(first.path("sunrise").asText(""))
                .sunset(first.path("sunset").asText(""))
                .description(first.path("description").asText(""))
                .mainInfo(first.path("description").asText(""))
                .build();
    }
}
