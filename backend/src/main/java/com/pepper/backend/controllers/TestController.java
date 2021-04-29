package com.pepper.backend.controllers;

import com.pepper.backend.services.database.DatabaseService;
import com.pepper.backend.services.messaging.BotMessageHandlerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class TestController {

    private final DatabaseService databaseService;
    private final BotMessageHandlerService botMessageHandlerService;

    public TestController(DatabaseService databaseService, BotMessageHandlerService botMessageHandlerService) {
        this.databaseService = databaseService;
        this.botMessageHandlerService = botMessageHandlerService;
    }

    @GetMapping("test")
    public void test() {
        this.botMessageHandlerService.send("backend: hello pepper 1");
    }

}
