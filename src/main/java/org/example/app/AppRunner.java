package org.example.app;

import org.example.RabbitMQ.Recv;
import org.example.facade.WeatherSystemFacade;

public class AppRunner {

    public static void main(String[] args) throws Exception {
        WeatherSystemFacade facade = new WeatherSystemFacade();
        facade.start();

    }
}
