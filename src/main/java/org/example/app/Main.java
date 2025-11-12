package org.example.app;
import com.fasterxml.jackson.databind.JsonNode;
import org.example.RabbitMQ.Recv;

public class Main {
    public static void main(String[] args) {
        JsonNode openJson = Recv.getOpenWeatherJson();
        if (openJson != null) {
            System.out.println(openJson.toPrettyString()); // красиво выводим JSON
        }
    }
}
