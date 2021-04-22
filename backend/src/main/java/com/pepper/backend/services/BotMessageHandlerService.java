package com.pepper.backend.services;

import com.pepper.backend.controllers.BotCommunicationController;
import org.springframework.stereotype.Service;

@Service
public class BotMessageHandlerService {

    private final BotCommunicationController botCommunicationController;

    public BotMessageHandlerService(BotCommunicationController botCommunicationController) {
        this.botCommunicationController = botCommunicationController;
    }

    public void send(String message) {
        this.botCommunicationController.publish(message);
    }

    public void handle(String message) {
        System.out.println(message);
    }
}
