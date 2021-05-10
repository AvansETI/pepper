package com.pepper.backend.model.protocol.bot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BotMessage {

    private String id;

    private String botId;

    private Person person;

    private String personId;

    private Task task;

    private String data;

}
