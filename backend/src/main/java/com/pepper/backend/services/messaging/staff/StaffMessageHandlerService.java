package com.pepper.backend.services.messaging.staff;

import com.pepper.backend.controllers.StaffCommunicationController;
import com.pepper.backend.model.Patient;
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

import java.security.GeneralSecurityException;
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

    public void send(String senderId, Person person, String personId, Task task, String taskId, String data) {
        String message = this.messageParser.stringify(Sender.PLATFORM, senderId, person, personId, task, taskId, data);

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
            case PATIENT_ID -> {
                LOG.info("Get patient id request");

                this.sendIds(this.databaseService.findPatientIds());
            }
            case PATIENT -> {
                LOG.info("Get patient request");

                if (message.getPerson() != Person.PATIENT) {
                    break;
                }

                if (message.getPersonId().equals("")) {
                    break;
                }

                this.sendPatient(this.databaseService.findPatientById(message.getPersonId()));

            }
            case QUESTION -> {
                LOG.info("New question: " + message.getData());
            }
            default -> {
                LOG.error("Unknown command: " + message.getTask());
            }
        }
    }

    public void sendPatient(Patient patient) {
        if (patient == null) {
            return;
        }

        this.send("1", Person.PATIENT, patient.getId(), Task.PATIENT_NAME, "1", String.valueOf(patient.getName()));
        this.send("1", Person.PATIENT, patient.getId(), Task.PATIENT_BIRTHDATE, "1", String.valueOf(patient.getBirthdate().toEpochDay()));
        this.send("1", Person.PATIENT, patient.getId(), Task.PATIENT_ALLERGY, "1", String.valueOf(patient.getAllergies()));
    }

    public void sendIds(Set<String> ids) {
        this.send("1", Person.PATIENT, "", Task.PATIENT_ID, "1", String.valueOf(ids));
    }


}
