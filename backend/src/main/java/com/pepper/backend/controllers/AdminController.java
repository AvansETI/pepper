package com.pepper.backend.controllers;

import com.google.gson.Gson;
import com.pepper.backend.model.User;
import com.pepper.backend.model.database.UserResponse;
import com.pepper.backend.services.database.DatabaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("api/v1/admin")
public class AdminController {

    private final DatabaseService databaseService;
    private final Gson gson;

    public AdminController(DatabaseService databaseService) {
        this.databaseService = databaseService;
        this.gson = new Gson();
    }

    @PostMapping("users")
    public ResponseEntity<Map<String, Object>> postUser(@RequestBody String body) {
        User user = gson.fromJson(body, User.class);
        UserResponse response = this.databaseService.saveUser(user);

        switch (response) {
            case OK -> {
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("status", "success");

                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            }
            case USERNAME_ALREADY_USED -> {
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("status", "failed");
                responseBody.put("reason", "username already in use");

                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }
            default -> {
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("status", "failed");
                responseBody.put("reason", "internal server error");

                return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

    }

}
