package org.example.app;

import org.example.RabbitMQ.Recv;
import org.example.RabbitMQ.Send;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Send send = new Send();

        // этот блок отвечает за вывод данных о погоде запуская Recv
        new Thread(() -> {
            try {
                Recv.startListener();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();


        //этот блок кода отвечает за отправку названии городов
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("City: ");
            String city = sc.nextLine();
            // новый поток для метода отправки городов Send
            new Thread(() -> send.sendCity(city)).start();
        }
    }
}
