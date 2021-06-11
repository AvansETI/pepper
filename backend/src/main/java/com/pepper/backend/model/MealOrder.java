package com.pepper.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("mealOrder")
public class MealOrder {

    @Id
    private String id;

    private String patientId;

    private String mealId;

    private LocalDateTime timestamp;

}
