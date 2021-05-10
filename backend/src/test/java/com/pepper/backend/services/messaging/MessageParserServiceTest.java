package com.pepper.backend.services.messaging;

import com.pepper.backend.model.protocol.bot.BotMessage;
import com.pepper.backend.model.protocol.bot.Person;
import com.pepper.backend.model.protocol.bot.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageParserServiceTest {

    MessageParserService messageParser;

    @BeforeEach
    void setup () {
        this.messageParser = new MessageParserService();
    }

    @Test
    void toBotMessage_test() {
        String message = "BOT:3:PATIENT:5:FEEDBACK#{bla: bla# bla}";
        BotMessage expected = BotMessage.builder()
                .id(null)
                .botId("3")
                .person(Person.PATIENT)
                .personId("5")
                .task(Task.FEEDBACK)
                .data("bla: bla# bla")
                .build();

        AtomicReference<BotMessage> botMessage = new AtomicReference<>();
        assertDoesNotThrow(() -> {
            botMessage.set(this.messageParser.toBotMessage(message));
        });

        assertEquals(expected, botMessage.get());
    }

    @Test
    void createMessage_test() {
        String expected = "PLATFORM:3:PATIENT:5:FEEDBACK#{bla# bla :bla}";
        String message = this.messageParser.createMessage("3", Person.PATIENT, "5", Task.FEEDBACK, "bla# bla :bla");

        assertEquals(expected, message);
    }

}
