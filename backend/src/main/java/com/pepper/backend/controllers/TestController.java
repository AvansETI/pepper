package com.pepper.backend.controllers;

import com.pepper.backend.services.database.DatabaseService;
import com.pepper.backend.services.messaging.BotMessageHandlerService;
import com.pepper.backend.services.messaging.StaffMessageHandlerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("api")
public class TestController {

    private final BotMessageHandlerService botMessageHandler;
    private final StaffMessageHandlerService staffMessageHandler;
    private final DatabaseService databaseService;

    public TestController(BotMessageHandlerService botMessageHandler, StaffMessageHandlerService staffMessageHandler, DatabaseService databaseService) {
        this.botMessageHandler = botMessageHandler;
        this.staffMessageHandler = staffMessageHandler;
        this.databaseService = databaseService;
    }

    @GetMapping("test")
    public void test() {
//        this.botMessageHandler.send("3", Person.GENERAL, "", Task.FEEDBACK_STATUS, "6", "data frf4654");
//        this.staffMessageHandler.send("3", Person.PATIENT, "", Task.PATIENT_NAME, "6", "data frf4654");
        //System.out.println(this.databaseService.findQuestionById("1"));
    }

}
