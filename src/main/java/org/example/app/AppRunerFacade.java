package org.example.app;

import org.example.RabbitMQ.Recv;
import org.example.RabbitMQ.Send;
import org.example.telegrambot.Bot;

public class AppRunerFacade {
    Recv recv = new Recv();
    Bot bot = new Bot();
    Send send = new Send();

}
