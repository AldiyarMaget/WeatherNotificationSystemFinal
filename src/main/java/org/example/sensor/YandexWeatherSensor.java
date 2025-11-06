package org.example.sensor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.core.WeatherData;
import org.example.core.exceptions.SensorException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class YandexWeatherSensor implements Sensor {
    private static final String URL = "https://api.weather.yandex.ru/graphql/query";
    private final String apiKey;
    private final double lat;
    private final double lon;
    private final HttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public YandexWeatherSensor(String apiKey, double lat, double lon) {
        this.apiKey = apiKey;
        this.lat = lat;
        this.lon = lon;
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public WeatherData read() throws SensorException {
        if (apiKey == null || apiKey.isBlank()) {
            throw new SensorException("Yandex API key is missing (null or blank).");
        }

        String query = ""
                + "query Weather($lat: Float!, $lon: Float!) { "
                + "  weatherByPoint(request: { lat: $lat, lon: $lon }) { "
                + "    now { temperature humidity pressure } "
                + "  } "
                + "}";

        Map<String, Object> body = new HashMap<>();
        body.put("query", query);
        Map<String, Object> variables = new HashMap<>();
        variables.put("lat", lat);
        variables.put("lon", lon);
        body.put("variables", variables);

        String bodyJson;
        try {
            bodyJson = mapper.writeValueAsString(body);
        } catch (Exception ex) {
            throw new SensorException("Failed to build GraphQL request body", ex);
        }

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json")
                .header("X-Yandex-Weather-Key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                .build();

        try {
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            int code = resp.statusCode();
            String respBody = resp.body();

            if (code != 200) {
                throw new SensorException("Yandex HTTP error " + code + ". Body: " + safeBodySnippet(respBody));
            }

            JsonNode root = mapper.readTree(respBody);
            JsonNode now = root.path("data").path("weatherByPoint").path("now");

            if (now.isMissingNode() || now.isNull()) {
                throw new SensorException("Missing 'now' in response. Body: " + safeBodySnippet(respBody));
            }

            double temp = asDoubleOrNaN(now, "temperature");
            double hum = asDoubleOrNaN(now, "humidity");

            double pressure = Double.NaN;
            if (!now.path("pressure").isMissingNode() && !now.path("pressure").isNull()) {
                pressure = asDoubleOrNaN(now, "pressure"); // скорее всего hPa
            }

            if (Double.isNaN(temp) || Double.isNaN(hum)) {
                throw new SensorException("Missing temperature/humidity in response. Body: " + safeBodySnippet(respBody));
            }

            return new WeatherData(temp, hum, pressure, Instant.now());
        } catch (IOException e) {
            throw new SensorException("Failed to parse Yandex response", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SensorException("HTTP request interrupted", e);
        }
    }

    private static String safeBodySnippet(String body) {
        if (body == null) return "<empty>";
        int max = 1200;
        return body.length() <= max ? body : body.substring(0, max) + "...";
    }

    private static double asDoubleOrNaN(JsonNode node, String field) {
        JsonNode v = node.path(field);
        if (v.isMissingNode() || v.isNull()) return Double.NaN;
        if (v.isNumber()) return v.asDouble();
        try { return Double.parseDouble(v.asText()); } catch (Exception ex) { return Double.NaN; }
    }
}
