package org.example.app;

import org.example.RabbitMQ.Recv;
import org.example.RabbitMQ.Send;
import org.example.telegrambot.Bot;

public class Main {
    public static void main(String[] args) throws Exception {

        new Thread(() -> {
            try {
                Recv.startListening();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        Send.sendRequest("aktau","current");
        Send.sendRequest("aktau","3 hour");
        Send.sendRequest("aktau","today");
        Send.sendRequest("aktau","tomorrow");
        Send.sendRequest("aktau","week");

    }
}
