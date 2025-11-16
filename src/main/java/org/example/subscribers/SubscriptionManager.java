package org.example.subscribers;

import java.util.List;

public class SubscriptionManager {
    private static final SubscriptionDB db = new SubscriptionDB();

    public static List<SubscriptionDB.Subscription> getAllSubscriptions() {
        return db.getAllSubscriptions();
    }

    public static List<SubscriptionDB.Subscription> getSubscriptions(long chatId) {
        return db.getSubscriptions(chatId);
    }

    public static SubscriptionDB.Subscription addSubscription(
            long chatId,
            String city,
            String interval
    ){
        db.addSubscription(chatId, city, interval);
        List<SubscriptionDB.Subscription> list = db.getSubscriptions(chatId);
        return list.get(list.size() - 1);
    }

    public static boolean removeSubscription(long chatId, int id) {
        db.removeSubscription(id);
        return true;
    }
}
