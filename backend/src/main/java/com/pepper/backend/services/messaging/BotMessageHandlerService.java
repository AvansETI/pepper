package com.pepper.backend.services.messaging;

import com.pepper.backend.controllers.BotCommunicationController;
import com.pepper.backend.model.*;
import com.pepper.backend.model.messaging.bot.BotMessage;
import com.pepper.backend.model.messaging.bot.Person;
import com.pepper.backend.model.messaging.bot.Sender;
import com.pepper.backend.model.messaging.bot.Task;
import com.pepper.backend.services.database.DatabaseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Arrays;
import java.util.HashSet;

@Service
public class BotMessageHandlerService {

    @Value("${encryption.password}")
    private String encryptionPassword;

    private final BotCommunicationController botCommunicationController;
    private final DatabaseService databaseService;
    private final BotMessageParserService messageParser;
    private final MessageEncryptorService messageEncryptor;

    public BotMessageHandlerService(BotCommunicationController botCommunicationController, DatabaseService databaseService, BotMessageParserService messageParser, MessageEncryptorService messageEncryptor) {
        this.botCommunicationController = botCommunicationController;
        this.databaseService = databaseService;
        this.messageParser = messageParser;
        this.messageEncryptor = messageEncryptor;
    }

    public void send(String senderId, Person person, String personId, Task task, String taskId, String data) {
        String message = this.messageParser.createMessage(Sender.PLATFORM, senderId, person, personId, task, taskId, data);

        try {
            message = this.messageEncryptor.encrypt(message, this.encryptionPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        this.botCommunicationController.publish(message);
    }

    public void handle(String message) {
//        try {
//            message = this.messageEncryptor.decrypt(message, this.encryptionPassword);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }

        BotMessage botMessage;
        try {
            botMessage = this.messageParser.toBotMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (botMessage.getSender() == Sender.PLATFORM) {
            return;
        }

        this.handleBotMessage(botMessage);
    }

    public void handleBotMessage(BotMessage message) {

        switch (message.getTask()) {
            case FEEDBACK_STATUS -> {
                this.databaseService.saveFeedback(Feedback.builder()
                        .id(message.getTaskId())
                        .patientId(message.getPersonId())
                        .status(Status.valueOf(message.getData()))
                        .build());
            }
            case FEEDBACK_EXPLANATION -> {
                this.databaseService.saveFeedback(Feedback.builder()
                        .id(message.getTaskId())
                        .patientId(message.getPersonId())
                        .explanation(message.getData())
                        .build());
            }
            case FEEDBACK_TIMESTAMP -> {
                this.databaseService.saveFeedback(Feedback.builder()
                        .id(message.getTaskId())
                        .patientId(message.getPersonId())
                        .timestamp(LocalDateTime.ofEpochSecond(Long.parseLong(message.getData()), 0, ZoneOffset.UTC))
                        .build());
            }
            case MEAL_ORDER_MEAL -> {
                this.databaseService.saveMealOrder(MealOrder.builder()
                        .id(message.getTaskId())
                        .patientId(message.getPersonId())
                        .meal(message.getData())
                        .build());
            }
            case MEAL_ORDER_TIMESTAMP -> {
                this.databaseService.saveMealOrder(MealOrder.builder()
                        .id(message.getTaskId())
                        .patientId(message.getPersonId())
                        .timestamp(LocalDateTime.ofEpochSecond(Long.parseLong(message.getData()), 0, ZoneOffset.UTC))
                        .build());
            }
            case ANSWER -> {
                System.out.println("answer: " + message.getData());
            }
            case QUESTION -> {
                System.out.println("question: " + message.getData());
            }
            case REMINDER -> {
                System.out.println("reminder: " + message.getData());
            }
            case PATIENT_NAME -> {
                this.databaseService.savePatient(Patient.builder()
                        .id(message.getPersonId())
                        .name(message.getData())
                        .build());
            }
            case PATIENT_BIRTHDATE -> {
                this.databaseService.savePatient(Patient.builder()
                        .id(message.getPersonId())
                        .birthdate(LocalDate.ofEpochDay(Long.parseLong(message.getData())))
                        .build());
            }
            case PATIENT_ALLERGY -> {
                this.databaseService.savePatient(Patient.builder()
                        .id(message.getPersonId())
                        .allergies(new HashSet<>(Arrays.asList(Allergy.valueOf(message.getData()))))
                        .build());
            }
        }

    }

}
