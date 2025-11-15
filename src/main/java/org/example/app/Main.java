package org.example.app;

import org.example.RabbitMQ.Recv;
import org.example.RabbitMQ.Send;
import org.example.telegrambot.Bot;

public class Main {
    public static void main(String[] args) throws Exception {


        //{"Name":"GoogleWeather","Coord":{"lon":71.43,"lat":51.17},"Main":{"temp":0,"feels_like":0,"temp_min":0,"temp_max":0,"humidity":0},"Sys":{"country":"Asia/Almaty","sunrise":"","sunset":""},"Weather":null,"Date":"","Days":[{"Name":"","Coord":{"lon":0,"lat":0},"Main":{"temp":-1,"feels_like":-1,"temp_min":-4.1,"temp_max":2.6,"humidity":0},"Sys":{"country":"Asia/Almaty","sunrise":"2025-11-15T02:31:02.057700453Z","sunset":"2025-11-15T11:26:05.050524044Z"},"Weather":[{"mainInfo":"Mostly sunny","description":"Clear"}],"Date":"2025-11-15","Days":null},{"Name":"","Coord":{"lon":0,"lat":0},"Main":{"temp":0,"feels_like":0,"temp_min":-4.3,"temp_max":3.3,"humidity":0},"Sys":{"country":"Asia/Almaty","sunrise":"2025-11-16T02:32:43.257978322Z","sunset":"2025-11-16T11:24:46.228318351Z"},"Weather":[{"mainInfo":"Sunny","description":"Clear"}],"Date":"2025-11-16","Days":null}]}
        /*new Thread(() -> {
            try {
                Recv.startListening();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        String request = """
                {
                  "city": "aktau",
                  "type": "current",
                  "period": 5,
                  "station": "google"
                }
                """;

        Send.sendRequest(request);

        System.out.println("[MAIN] Request sent.");
*/
        Bot bot = new Bot();


    }
}
