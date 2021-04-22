package com.pepper.backend.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pepper.backend.controllers.BotCommunicationController;
import com.pepper.backend.model.bot.BotMessage;
import org.springframework.stereotype.Service;

@Service
public class BotMessageHandlerService {

    private final BotCommunicationController botCommunicationController;
    private final DatabaseService databaseService;
    private final Gson gson;

    public BotMessageHandlerService(BotCommunicationController botCommunicationController, DatabaseService databaseService) {
        this.botCommunicationController = botCommunicationController;
        this.databaseService = databaseService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(BotMessage.class, new BotMessage())
                .create();
    }

    public void send(String message) {
        this.botCommunicationController.publish(message);
    }

    public void handle(String message) {
        BotMessage botMessage = this.gson.fromJson(message, BotMessage.class);
        this.databaseService.writeBotMessage(botMessage);
    }
}
