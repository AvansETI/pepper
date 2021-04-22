package com.pepper.backend.model.staff;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "staffMessages")
public class StaffMessage {

    @Id
    private String id;

    private String staffId;

    private String message;

    private LocalDateTime timestamp;

}
