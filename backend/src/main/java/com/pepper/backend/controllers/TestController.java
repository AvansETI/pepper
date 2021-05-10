package com.pepper.backend.controllers;

import com.pepper.backend.model.bot.Person;
import com.pepper.backend.model.bot.Task;
import com.pepper.backend.services.messaging.BotMessageHandlerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class TestController {

    private final BotMessageHandlerService botMessageHandlerService;

    public TestController(BotMessageHandlerService botMessageHandlerService) {
        this.botMessageHandlerService = botMessageHandlerService;
    }

    @GetMapping("test")
    public void test() {
        this.botMessageHandlerService.send("3", Person.GENERAL, "", Task.FEEDBACK, "data frf4654");
    }

}
