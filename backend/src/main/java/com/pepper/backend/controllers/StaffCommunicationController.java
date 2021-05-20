package com.pepper.backend.controllers;

import com.pepper.backend.services.messaging.staff.StaffMessageHandlerService;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class StaffCommunicationController {

    private final SimpMessagingTemplate messagingTemplate;
    private final StaffMessageHandlerService messageHandler;

    public StaffCommunicationController(@Lazy StaffMessageHandlerService messageHandler, SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.messageHandler = messageHandler;
    }

    public boolean send(String message) {
        this.messagingTemplate.convertAndSend("/topic/data", message);

        return true;
    }

    @MessageMapping("/data")
    public void onMessageReceive(String message) {
        this.messageHandler.handle(message);
    }

}
