package com.pepper.backend.services.messaging.staff;

import com.pepper.backend.controllers.StaffCommunicationController;
import com.pepper.backend.model.messaging.Message;
import com.pepper.backend.model.messaging.Person;
import com.pepper.backend.model.messaging.Sender;
import com.pepper.backend.model.messaging.Task;
import com.pepper.backend.services.messaging.MessageEncryptorService;
import com.pepper.backend.services.messaging.MessageParserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;

@Service
public class StaffMessageHandlerService {

    @Value("${encryption.enabled}")
    private boolean encryptionEnabled;

    @Value("${encryption.password}")
    private String encryptionPassword;

    private final StaffCommunicationController staffCommunicationController;
    private final MessageParserService messageParser;
    private final MessageEncryptorService messageEncryptor;

    public StaffMessageHandlerService(StaffCommunicationController staffCommunicationController, MessageParserService messageParser, MessageEncryptorService messageEncryptor) {
        this.staffCommunicationController = staffCommunicationController;
        this.messageParser = messageParser;
        this.messageEncryptor = messageEncryptor;
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

        this.staffCommunicationController.send(message);
    }

    public void handle(String message) {
        if (this.encryptionEnabled) {
            try {
                message = this.messageEncryptor.decrypt(message, this.encryptionPassword);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
                return;
            }
        }

        Message staffMessage;
        try {
            staffMessage = this.messageParser.parse(message);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (staffMessage.getSender() != Sender.STAFF) {
            return;
        }

        this.handleStaffMessage(staffMessage);
    }

    public void handleStaffMessage(Message message) {
        System.out.println("Received staff message: " + message);
    }

}
