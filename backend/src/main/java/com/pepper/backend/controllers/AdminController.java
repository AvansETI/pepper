package com.pepper.backend.controllers;

import com.google.gson.Gson;
import com.pepper.backend.model.Allergy;
import com.pepper.backend.model.Meal;
import com.pepper.backend.model.Patient;
import com.pepper.backend.model.User;
import com.pepper.backend.model.database.UserResponse;
import com.pepper.backend.services.database.DatabaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@RestController
@RequestMapping("api/v1/admin")
public class AdminController {

    private final DatabaseService databaseService;
    private final Gson gson;

    public AdminController(DatabaseService databaseService) {
        this.databaseService = databaseService;
        this.gson = new Gson();
    }

    @GetMapping("test")
    public void test() {
        this.databaseService.testSavePatient(new Patient(
                "-1",
                "Bas",
                LocalDate.of(2000, 6, 5),
                Set.of(Allergy.GLUTEN)));

        this.databaseService.testSaveMeal(new Meal("-1", "Appelpannenkoeken", "met stukjes appel", "600", Set.of(Allergy.GLUTEN), "http://2.bp.blogspot.com/_ynzQW6_AKLg/S8sFvDZoH4I/AAAAAAAAAgQ/u88lJIbNqGQ/s1600/appelpannenkoek.jpg"));
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
