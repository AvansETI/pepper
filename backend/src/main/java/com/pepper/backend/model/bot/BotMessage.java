package com.pepper.backend.model.bot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "botMessages")
public class BotMessage {

    @Id
    private String id;

    private String botId;

    private Person person;

    private String personId;

    private Task task;

    private String data;

}
