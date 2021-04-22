package com.pepper.backend.model.bot;

import com.google.gson.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "botMessages")
public class BotMessage implements JsonDeserializer<BotMessage> {

    @Id
    private String id;

    private String botId;

    private String message;

    private LocalDateTime timestamp;

    @Override
    public BotMessage deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        JsonObject root = element.getAsJsonObject();

        return BotMessage.builder()
                .botId(context.deserialize(root.get("botId"), String.class))
                .message(context.deserialize(root.get("message"), String.class))
                .timestamp(LocalDateTime.parse(context.deserialize(root.get("timestamp"), String.class), formatter))
                .build();
    }
}
