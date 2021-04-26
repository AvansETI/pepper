package com.pepper.backend.services.messaging;

import com.pepper.backend.controllers.StaffCommunicationController;
import org.springframework.stereotype.Service;

@Service
public class StaffMessageHandlerService {

    private final StaffCommunicationController staffCommunicationController;

    public StaffMessageHandlerService(StaffCommunicationController staffCommunicationController) {
        this.staffCommunicationController = staffCommunicationController;
    }

    public void send(String message) {
        this.staffCommunicationController.send(message);
        System.out.println("Send WebSocket message: " + message);
    }

    public void handle(String message) {
        System.out.println("Received WebSocket message: " + message);
    }

}
