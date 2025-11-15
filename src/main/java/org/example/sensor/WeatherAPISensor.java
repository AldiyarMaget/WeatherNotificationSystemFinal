package org.example.sensor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.core.WeatherData;

import java.io.File;
import java.io.IOException;

public class WeatherAPISensor implements Sensor{
    @Override
    public WeatherData read() throws IOException {
        File file = new File("data/openweather.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(file);

        String city = root.get("city").asText();
        double temperature   = root.path("mainInfo").path("temp").asDouble(0.0);
        double feelsLike     = root.path("mainInfo").path("feels_like").asDouble(0.0);
        double humidity   = root.path("mainInfo").path("humidity").asDouble(0.0);

        String country = root.path("sys").path("country").asText(null);
        String sunrise = root.path("sys").path("sunrise").asText(null);
        String sunset = root.path("sys").path("sunset").asText(null);

        return WeatherData
                .builder()
                .city(city)
                .temperature(temperature)
                .feelsLike(feelsLike)
                .humidity(humidity)
                .sunrise(sunrise)
                .sunset(sunset)
                .country(country)
                .build();
    }
}
