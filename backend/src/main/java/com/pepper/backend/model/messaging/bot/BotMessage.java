package com.pepper.backend.model.messaging.bot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BotMessage {

    private Sender sender;

    private String botId;

    private Person person;

    private String personId;

    private Task task;

    private String data;

}
