package com.pepper.backend.model.messaging.staff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffMessage {

    private String id;

    private String staffId;

    private String message;

    private LocalDateTime timestamp;

}
