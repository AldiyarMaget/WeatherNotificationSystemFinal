package org.example.sensor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.core.WeatherData;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class GoogleWeatherTomorrowSensor implements Sensor {
    @Override
    public List<WeatherData> read() throws IOException {
        File file = new File("data/googleweathertomorrow.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(file);

        JsonNode second = root.get(1); // второй день
        return WeatherData.builder()
                .city(second.path("city").asText(""))
                .temperature(second.path("tempMax").asDouble(0))
                .feelsLike(second.path("feelsLikeMax").asDouble(0))
                .minTemperature(second.path("tempMin").asDouble(0))
                .maxTemperature(second.path("tempMax").asDouble(0))
                .humidity(second.path("humidity").asDouble(0))
                .sunrise(second.path("sunrise").asText(""))
                .sunset(second.path("sunset").asText(""))
                .description(second.path("description").asText(""))
                .mainInfo(second.path("description").asText(""))
                .build();
    }
}