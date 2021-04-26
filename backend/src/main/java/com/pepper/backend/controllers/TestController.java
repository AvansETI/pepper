package com.pepper.backend.controllers;

import com.pepper.backend.services.database.DatabaseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class TestController {

    private final DatabaseService databaseService;

    public TestController(DatabaseService databaseService) {

        this.databaseService = databaseService;
    }

    @GetMapping("test")
    public void test() {
        System.out.println(this.databaseService.readAllBotMessages());
    }

}
