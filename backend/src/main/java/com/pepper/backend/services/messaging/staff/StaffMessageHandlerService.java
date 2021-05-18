package com.pepper.backend.services.messaging.staff;

import com.pepper.backend.controllers.StaffCommunicationController;
import com.pepper.backend.services.messaging.MessageEncryptorService;
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
    private final MessageEncryptorService messageEncryptor;

    public StaffMessageHandlerService(StaffCommunicationController staffCommunicationController, MessageEncryptorService messageEncryptor) {
        this.staffCommunicationController = staffCommunicationController;
        this.messageEncryptor = messageEncryptor;
    }

    public void send(String message) {
        if (this.encryptionEnabled) {
            try {
                message = this.messageEncryptor.encrypt(message, this.encryptionPassword);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
                return;
            }
        }

        this.staffCommunicationController.send(message);
        System.out.println("Send staff message: " + message);
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

        System.out.println("Received staff message: " + message);
    }

}
