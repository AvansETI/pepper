package com.pepper.backend.controllers;

import com.pepper.backend.services.messaging.MessageEncryptorService;
import com.pepper.backend.services.messaging.StaffMessageHandlerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.security.GeneralSecurityException;

@Controller
public class StaffCommunicationController {

    @Value("${encryption.password}")
    private String encryptionPassword;

    private final SimpMessagingTemplate messagingTemplate;
    private final StaffMessageHandlerService messageHandler;
    private final MessageEncryptorService encryptionService;

    public StaffCommunicationController(@Lazy StaffMessageHandlerService messageHandler, MessageEncryptorService encryptionService, SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.messageHandler = messageHandler;
        this.encryptionService = encryptionService;
    }

    public void send(String message) {
        try {
            message = this.encryptionService.encrypt(message, this.encryptionPassword);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return;
        }

        this.messagingTemplate.convertAndSend("/topic/data", message);
    }

    @MessageMapping("/data")
    public void onMessageReceive(String message) {
        try {
            message = this.encryptionService.decrypt(message, this.encryptionPassword);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return;
        }

        this.messageHandler.handle(message);
    }

}
