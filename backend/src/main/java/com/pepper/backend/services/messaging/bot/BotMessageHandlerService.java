package com.pepper.backend.services.messaging.bot;

import com.pepper.backend.controllers.BotCommunicationController;
import com.pepper.backend.model.*;
import com.pepper.backend.model.messaging.Message;
import com.pepper.backend.model.messaging.Person;
import com.pepper.backend.model.messaging.Sender;
import com.pepper.backend.model.messaging.Task;
import com.pepper.backend.services.database.DatabaseService;
import com.pepper.backend.services.messaging.MessageEncryptorService;
import com.pepper.backend.services.messaging.MessageParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Arrays;
import java.util.HashSet;

@Service
public class BotMessageHandlerService {

    private static final Logger LOG = LoggerFactory.getLogger(BotMessageHandlerService.class);

    @Value("${encryption.enabled}")
    private boolean encryptionEnabled;

    @Value("${encryption.password}")
    private String encryptionPassword;

    private final BotCommunicationController botCommunicationController;
    private final DatabaseService databaseService;
    private final MessageParserService messageParser;
    private final MessageEncryptorService messageEncryptor;

    public BotMessageHandlerService(BotCommunicationController botCommunicationController, DatabaseService databaseService, MessageParserService messageParser, MessageEncryptorService messageEncryptor) {
        this.botCommunicationController = botCommunicationController;
        this.databaseService = databaseService;
        this.messageParser = messageParser;
        this.messageEncryptor = messageEncryptor;
    }

    public void send(String senderId, Person person, String personId, Task task, String taskId, String data) {
        String message = this.messageParser.stringify(Sender.PLATFORM, senderId, person, personId, task, taskId, data);

        if (this.encryptionEnabled) {
            try {
                message = this.messageEncryptor.encrypt(message, this.encryptionPassword);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        if (this.botCommunicationController.publish(message)) {
            LOG.info("Send message to bot: " + message);
        } else {
            LOG.error("Failed to send message to bot");
        }
    }

    public void handle(String message) {
        if (this.encryptionEnabled) {
            try {
                message = this.messageEncryptor.decrypt(message, this.encryptionPassword);
            } catch (Exception e) {
                LOG.error("Failed to decrypt message: " + message);
                return;
            }
        }

        Message botMessage;
        try {
            botMessage = this.messageParser.parse(message);
        } catch (Exception e) {
            LOG.error("Failed to parse message: " + message);
            return;
        }

        if (botMessage.getSender() != Sender.BOT) {
            return;
        }

        this.handleBotMessage(botMessage);
    }

    public void handleBotMessage(Message message) {

        switch (message.getTask()) {
            case FEEDBACK_STATUS -> {
                LOG.info("New feedback status: " + message.getData());
                this.databaseService.saveFeedback(Feedback.builder()
                        .id(message.getTaskId())
                        .patientId(message.getPersonId())
                        .status(Status.valueOf(message.getData()))
                        .build());
            }
            case FEEDBACK_EXPLANATION -> {
                LOG.info("New feedback explanation: " + message.getData());
                this.databaseService.saveFeedback(Feedback.builder()
                        .id(message.getTaskId())
                        .patientId(message.getPersonId())
                        .explanation(message.getData())
                        .build());
            }
            case FEEDBACK_TIMESTAMP -> {
                LOG.info("New feedback timestamp: " + message.getData());
                this.databaseService.saveFeedback(Feedback.builder()
                        .id(message.getTaskId())
                        .patientId(message.getPersonId())
                        .timestamp(LocalDateTime.ofEpochSecond(Long.parseLong(message.getData()), 0, ZoneOffset.UTC))
                        .build());
            }
            case MEAL_ORDER_MEAL -> {
                LOG.info("New meal order meal: " + message.getData());
                this.databaseService.saveMealOrder(MealOrder.builder()
                        .id(message.getTaskId())
                        .patientId(message.getPersonId())
                        .meal(message.getData())
                        .build());
            }
            case MEAL_ORDER_TIMESTAMP -> {
                LOG.info("New meal order timestamp: " + message.getData());
                this.databaseService.saveMealOrder(MealOrder.builder()
                        .id(message.getTaskId())
                        .patientId(message.getPersonId())
                        .timestamp(LocalDateTime.ofEpochSecond(Long.parseLong(message.getData()), 0, ZoneOffset.UTC))
                        .build());
            }
            case ANSWER -> {
                LOG.info("New answer: " + message.getData());
            }
            case QUESTION -> {
                LOG.info("New question: " + message.getData());
            }
            case REMINDER -> {
                LOG.info("New reminder: " + message.getData());
            }
            case PATIENT_NAME -> {
                LOG.info("New patient name: " + message.getData());
                this.databaseService.savePatient(Patient.builder()
                        .id(message.getPersonId())
                        .name(message.getData())
                        .build());
            }
            case PATIENT_BIRTHDATE -> {
                LOG.info("New patient birthdate: " + message.getData());
                this.databaseService.savePatient(Patient.builder()
                        .id(message.getPersonId())
                        .birthdate(LocalDate.ofEpochDay(Long.parseLong(message.getData())))
                        .build());
            }
            case PATIENT_ALLERGY -> {
                LOG.info("New patient allergy: " + message.getData());
                this.databaseService.savePatient(Patient.builder()
                        .id(message.getPersonId())
                        .allergies(new HashSet<>(Arrays.asList(Allergy.valueOf(message.getData()))))
                        .build());
            }
            default -> {
                LOG.error("Unknown command: " + message.getTask());
            }
        }

    }

}
