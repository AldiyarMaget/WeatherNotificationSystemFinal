package org.example.sensor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.RabbitMQ.Recv;
import org.example.RabbitMQ.Send;
import org.example.core.WeatherData;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static java.lang.Thread.sleep;

public class GoogleWeatherTomorrowSensor implements Sensor {
    @Override
    public List<WeatherData> read(String city) throws IOException {
        try {
            Send.sendRequest(city,"tomorrow");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            Recv.startListening();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        File file = new File("data/googleweathertomorrow.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(file).path("data");

        if (root.isArray() && root.size() > 1) {
            JsonNode tomorrowNode = root.get(1);
            return Collections.singletonList(WeatherData.builder()
                    .temperature(tomorrowNode.path("tempMax").asDouble(0.0))
                    .feelsLike(tomorrowNode.path("feelsLikeMax").asDouble(0.0))
                    .minTemperature(tomorrowNode.path("tempMin").asDouble(0.0))
                    .maxTemperature(tomorrowNode.path("tempMax").asDouble(0.0))
                    .humidity(tomorrowNode.path("humidity").asDouble(0.0))
                    .cloudCover(tomorrowNode.path("cloudCover").asInt(0))
                    .windSpeed(tomorrowNode.path("windSpeed").asDouble(0.0))
                    .windDirection(tomorrowNode.path("windDirection").asText(""))
                    .description(tomorrowNode.path("description").asText(""))
                    .mainInfo(tomorrowNode.path("description").asText(""))
                    .sunrise(tomorrowNode.path("sunrise").asText(""))
                    .sunset(tomorrowNode.path("sunset").asText(""))
                    .date(tomorrowNode.path("date").asText(""))
                    .precipitationPercent(tomorrowNode.path("precipitationPercent").asInt(0))
                    .build());
        }
        return Collections.emptyList();
    }
}
