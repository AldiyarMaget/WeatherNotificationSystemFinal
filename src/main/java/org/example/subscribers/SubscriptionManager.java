package org.example.subscribers;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SubscriptionManager {
    private static final List<Subscription> subscriptions = new ArrayList<>();
    private static final AtomicInteger idCounter = new AtomicInteger(1);

    public static List<Subscription> callSubscribeList(long chatId) {
        List<Subscription> list = new ArrayList<>();
        for (Subscription s : subscriptions) {
            if (s.getChatId() == chatId) list.add(s);
        }
        return list;
    }

    public static Subscription addSubscription(long chatId, String city, String interval) {
        int id = idCounter.getAndIncrement();
        Subscription s = new Subscription(chatId, city, interval, id);
        subscriptions.add(s);
        return s;
    }

    public static boolean removeSubscription(long chatId, int id) {
        return subscriptions.removeIf(s -> s.getChatId() == chatId && s.getId() == id);
    }
}
