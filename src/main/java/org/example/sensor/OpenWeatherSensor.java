package org.example.sensor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.core.WeatherData;

import java.io.File;
import java.io.IOException;

public class OpenWeatherSensor implements Sensor{
    @Override
    public WeatherData read() throws IOException {
        File file = new File("data/openweather.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(file);

        String city = root.get("city").asText();
        double temperature   = root.path("main").path("temp").asDouble(0.0);
        double feelsLike     = root.path("main").path("feels_like").asDouble(0.0);
        double minTemperature= root.path("main").path("temp_min").asDouble(0.0);
        double maxTemperature= root.path("main").path("temp_max").asDouble(0.0);
        double humidity   = root.path("main").path("humidity").asDouble(0.0);

        String country = root.path("sys").path("country").asText(null);
        String sunrise = root.path("sys").path("sunrise").asText(null);
        String sunset = root.path("sys").path("sunset").asText(null);
        String description = "";
        String mainInfo = "";
        if (root.path("weather").isArray() && !root.path("weather").isEmpty()) {
            description = root.path("weather").get(1).path("description").asText("");
            mainInfo = root.path("weather").get(0).path("main").asText("");
        }

        return WeatherData
                .builder()
                .city(city)
                .temperature(temperature)
                .feelsLike(feelsLike)
                .minTemperature(minTemperature)
                .maxTemperature(maxTemperature)
                .humidity(humidity)
                .sunrise(sunrise)
                .sunset(sunset)
                .country(country)
                .description(description)
                .mainInfo(mainInfo)
                .build();
    }
}
