package org.example.telegrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import java.util.List;


public class Bot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "WeatehrNotificationsSDPbot";
    }

    @Override
    public String getBotToken() {
        return "8513493773:AAGWu0U0hlT5_fhRq7UQto_FtDav68K2UzQ";
    }


    public void sendMainMenu(String chatId){
        SendMessage msg = new SendMessage(chatId, ".|.");

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();

        row1.add(new KeyboardButton("Weather"));
        row1.add(new KeyboardButton("Subscription"));

        row2.add(new KeyboardButton("Preferences"));

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setKeyboard(List.of(row1,row2));
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);

        msg.setReplyMarkup(keyboard);

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public void sendSubMenu(String chatId){
        SendMessage msg = new SendMessage(chatId, ".|.");

        InlineKeyboardButton sub = new InlineKeyboardButton("Podpisatca");
        sub.setCallbackData("podpisatca");

        InlineKeyboardButton unsub = new InlineKeyboardButton("OtPISKI");
        unsub.setCallbackData("otPISKI");

        InlineKeyboardButton listsub = new InlineKeyboardButton("Podpiski");
        listsub.setCallbackData("podpiski");


        List<List<InlineKeyboardButton>> rows = List.of(
                List.of(sub,unsub,listsub)
        );

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(rows);
        msg.setReplyMarkup(keyboard);

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public void subscriptionMenu(String chatId){
        SendMessage msg = new SendMessage(chatId, "Napishi Nazvanie Goroda");

    }

    public void sendText(Long who, String what){
        SendMessage sendMessage = SendMessage.builder()
                .chatId(who.toString())
                .text(what)
                .build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                var msg = update.getMessage();
                var text = msg.getText();
                var user = msg.getFrom();
                var id = user.getId();
                var chatId = msg.getChatId().toString();


                switch (text) {
                    case "/start" -> {
                        sendMainMenu(chatId);
                    }
                    /*case "Weather" -> {
                        SendWeatherMenu(chatId);
                    }*/
                    case "Subscription" -> {
                        sendSubMenu(chatId);
                    }
                    default -> sendText(id, "stupid nigga");
                }
            }
            else if (update.hasCallbackQuery()){
                var callback = update.getCallbackQuery();
                var data = callback.getData();
                var chatId = callback.getMessage().getChatId().toString();

                switch (data) {
                    case "/podpisatca" -> {
                        execute(new SendMessage(chatId,"podpisatca"));
                    }
                    case "/otPISKI" -> {
                        execute(new SendMessage(chatId,"otPISKI"));
                    }
                }
            }
            execute(new SendMessage());
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }



    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        Bot bot = new Bot();
        botsApi.registerBot(bot);
    }

}