package com.pepper.backend.services.messaging;

import com.pepper.backend.controllers.BotCommunicationController;
import com.pepper.backend.model.protocol.bot.BotMessage;
import com.pepper.backend.model.protocol.bot.Person;
import com.pepper.backend.model.protocol.bot.Task;
import com.pepper.backend.services.database.DatabaseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BotMessageHandlerService {

    private final BotCommunicationController botCommunicationController;
    private final DatabaseService databaseService;
    private final MessageParserService messageParser;
    private final MessageEncryptorService messageEncryptor;

    @Value("${encryption.password}")
    private String encryptionPassword;

    public BotMessageHandlerService(BotCommunicationController botCommunicationController, DatabaseService databaseService, MessageParserService messageParser, MessageEncryptorService messageEncryptor) {
        this.botCommunicationController = botCommunicationController;
        this.databaseService = databaseService;
        this.messageParser = messageParser;
        this.messageEncryptor = messageEncryptor;
    }

    public void send(String botId, Person person, String personId, Task task, String data) {
        String message = messageParser.createMessage(botId, person, personId, task, data);

        try {
            message = this.messageEncryptor.encrypt(message, this.encryptionPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        this.botCommunicationController.publish(message);
    }

    public void handle(String message) {
        try {
            message = this.messageEncryptor.decrypt(message, this.encryptionPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (message.contains("PLATFORM")) {
            return;
        }

        BotMessage botMessage = null;
        try {
            botMessage = this.messageParser.toBotMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        System.out.println("RECEIVED: " + botMessage);
        //BotMessage botMessage = this.gson.fromJson(message, BotMessage.class);
        //this.databaseService.writeBotMessage(botMessage);
    }
}
