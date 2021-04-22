package com.pepper.backend.controllers;

import com.pepper.backend.services.BotMessageHandlerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class TestController {

    private final BotMessageHandlerService messageHandler;

    public TestController(BotMessageHandlerService messageHandler) {
        this.messageHandler = messageHandler;
    }

    @GetMapping("test")
    public void test() {
        messageHandler.send("hoi");
    }

}
