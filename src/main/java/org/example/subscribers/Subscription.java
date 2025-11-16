package org.example.subscribers;

public class Subscription {
    private final long chatId; //Айдишку тг юзера
    private final String city;
    private final String interval; // "hour", "day", "week"
    private final int id;

    public Subscription(long chatId, String city, String interval, int id) {
        this.chatId = chatId;
        this.city = city;
        this.interval = interval;
        this.id = id;
    }


    public long getChatId() { return chatId; }
    public String getCity() { return city; }
    public String getInterval() { return interval; }
    public int getId() { return id; }

    @Override
    public String toString() {
        return String.format("ID: %d | City: %s | Interval: %s", id, city, interval);
    }
}
