package org.example.app;

import org.example.RabbitMQ.Recv;
import org.example.RabbitMQ.Send;

public class Main {
    public static void main(String[] args) throws Exception {

        new Thread(() -> {
            try {
                Recv.startListening();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();


    }
}
