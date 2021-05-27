package com.pepper.backend.services.messaging;

import com.pepper.backend.controllers.StaffCommunicationController;
import com.pepper.backend.model.Allergy;
import com.pepper.backend.model.Meal;
import com.pepper.backend.model.Patient;
import com.pepper.backend.model.Question;
import com.pepper.backend.model.database.Response;
import com.pepper.backend.model.messaging.Message;
import com.pepper.backend.model.messaging.Person;
import com.pepper.backend.model.messaging.Sender;
import com.pepper.backend.model.messaging.Task;
import com.pepper.backend.services.database.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

@Service
public class StaffMessageHandlerService {

    private static final Logger LOG = LoggerFactory.getLogger(StaffMessageHandlerService.class);

    @Value("${encryption.enabled}")
    private boolean encryptionEnabled;

    @Value("${encryption.password}")
    private String encryptionPassword;

    private final StaffCommunicationController staffCommunicationController;
    private final MessageParserService messageParser;
    private final MessageEncryptorService messageEncryptor;
    private final DatabaseService databaseService;

    public StaffMessageHandlerService(StaffCommunicationController staffCommunicationController, MessageParserService messageParser, MessageEncryptorService messageEncryptor, DatabaseService databaseService) {
        this.staffCommunicationController = staffCommunicationController;
        this.messageParser = messageParser;
        this.messageEncryptor = messageEncryptor;
        this.databaseService = databaseService;
    }

    public void send(Person person, String personId, Task task, String taskId, String data) {
        String message = this.messageParser.stringify(Sender.PLATFORM, "1", person, personId, task, taskId, data);

        if (this.encryptionEnabled) {
            try {
                message = this.messageEncryptor.encrypt(message, this.encryptionPassword);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
                return;
            }
        }

        if (this.staffCommunicationController.send(message)) {
            LOG.info("Send message to staff: " + message);
        } else {
            LOG.error("Failed to send message to staff");
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

        Message staffMessage;
        try {
            staffMessage = this.messageParser.parse(message);
        } catch (Exception e) {
            LOG.error("Failed to parse message: " + message);
            return;
        }

        if (staffMessage.getSender() != Sender.STAFF) {
            return;
        }

        this.handleStaffMessage(staffMessage);
    }

    public void handleStaffMessage(Message message) {

        switch (message.getTask()) {
            case PATIENT -> {
                LOG.info("Get patient request");

                if (message.getPerson() != Person.PATIENT) {
                    break;
                }

                if (message.getPersonId().equals("-1")) {
                    break;
                }

                this.sendPatient(this.databaseService.findPatient(message.getPersonId()), message.getTaskId());
            }
            case PATIENT_ID -> {
                if (message.getPerson() == Person.PATIENT) {
                    LOG.info("Get patient ids request");

                    this.sendPersonIds(this.databaseService.findPatientIds(), message.getTaskId());
                }
            }
            case MEAL_NAME -> {
                LOG.info("New meal name");

                Response response = this.databaseService.saveMeal(Meal.builder()
                        .id(message.getTaskId())
                        .name(message.getData())
                        .build());

                if (response.isNew()) {
                    this.sendId(Person.PATIENT, message.getPersonId(), Task.MEAL_ID, response.getId(), response.getId());
                }
            }
            case MEAL_DESCRIPTION -> {
                LOG.info("New meal description");

                Response response = this.databaseService.saveMeal(Meal.builder()
                        .id(message.getTaskId())
                        .description(message.getData())
                        .build());

                if (response.isNew()) {
                    this.sendId(Person.PATIENT, message.getPersonId(), Task.MEAL_ID, response.getId(), response.getId());
                }
            }
            case MEAL_CALORIES -> {
                LOG.info("New meal calories");

                Response response = this.databaseService.saveMeal(Meal.builder()
                        .id(message.getTaskId())
                        .calories(message.getData())
                        .build());

                if (response.isNew()) {
                    this.sendId(Person.PATIENT, message.getPersonId(), Task.MEAL_ID, response.getId(), response.getId());
                }
            }
            case MEAL_ALLERGIES -> {
                LOG.info("New meal allergies");

                String[] allergiesString = message.getData().replace(" ", "").substring(1, message.getData().length() - 1).split(",");
                Set<Allergy> allergies = new HashSet<>();

                for (String allergyString : allergiesString) {
                    if (allergyString.equals("")) {
                        continue;
                    }
                    allergies.add(Allergy.valueOf(allergyString));
                }

                Response response = this.databaseService.saveMeal(Meal.builder()
                        .id(message.getTaskId())
                        .allergies(allergies)
                        .build());

                if (response.isNew()) {
                    this.sendId(Person.PATIENT, message.getPersonId(), Task.MEAL_ID, response.getId(), response.getId());
                }
            }
            case MEAL_IMAGE -> {
                LOG.info("New meal image");

                Response response = this.databaseService.saveMeal(Meal.builder()
                        .id(message.getTaskId())
                        .image(message.getData())
                        .build());

                if (response.isNew()) {
                    this.sendId(Person.PATIENT, message.getPersonId(), Task.MEAL_ID, response.getId(), response.getId());
                }
            }
            case QUESTION_TEXT -> {
                if (message.getPerson() == Person.PATIENT) {
                    LOG.info("Received new question for patient " + message.getPersonId() + ": " + message.getData());

                    Response response = this.databaseService.saveQuestion(Question.builder()
                            .id(message.getTaskId())
                            .patientId(message.getPersonId())
                            .text(message.getData())
                            .build());

                    if (response.isNew()) {
                        this.sendId(Person.PATIENT, message.getPersonId(), Task.QUESTION_ID, response.getId(), response.getId());
                    }
                }
            }
            case QUESTION_TIMESTAMP -> {
                if (message.getPerson() == Person.PATIENT) {
                    LOG.info("Received new question timestamp for patient " + message.getPersonId() + ": " + message.getData());

                    Response response = this.databaseService.saveQuestion(Question.builder()
                            .id(message.getTaskId())
                            .patientId(message.getPersonId())
                            .timestamp(LocalDateTime.ofEpochSecond(Long.parseLong(message.getData()), 0, ZoneOffset.UTC))
                            .build());

                    if (response.isNew()) {
                        this.sendId(Person.PATIENT, message.getPersonId(), Task.QUESTION_ID, response.getId(), response.getId());
                    }
                }
            }
            default -> {
                LOG.error("Unknown command: " + message.getTask());
            }
        }
    }

    public void sendPatient(Patient patient, String taskId) {
        if (patient == null) {
            return;
        }

        this.send(Person.PATIENT, patient.getId(), Task.PATIENT_NAME, taskId, patient.getName());
        this.send(Person.PATIENT, patient.getId(), Task.PATIENT_BIRTHDATE, taskId, String.valueOf(patient.getBirthdate().toEpochDay()));
        this.send(Person.PATIENT, patient.getId(), Task.PATIENT_ALLERGIES, taskId, String.valueOf(patient.getAllergies() == null ? new HashSet<>() : patient.getAllergies()));
    }

    public void sendPersonIds(Set<String> ids, String taskId) {
        this.send(Person.PATIENT, "-1", Task.PATIENT_ID, taskId, String.valueOf(ids));
    }

    public void sendId(Person person, String personId, Task task, String taskId, String id) {
        this.send(person, personId, task, taskId, id);
    }


}
