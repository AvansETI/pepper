package com.pepper.backend.services.messaging;

import com.pepper.backend.model.messaging.Message;
import com.pepper.backend.model.messaging.Person;
import com.pepper.backend.model.messaging.Sender;
import com.pepper.backend.model.messaging.Task;
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
        String message = "BOT:3:PATIENT:5:FEEDBACK_STATUS:8#{bla: bla# bla}";
        Message expected = Message.builder()
                .sender(Sender.BOT)
                .senderId("3")
                .person(Person.PATIENT)
                .personId("5")
                .task(Task.FEEDBACK_STATUS)
                .taskId("8")
                .data("bla: bla# bla")
                .build();

        AtomicReference<Message> botMessage = new AtomicReference<>();
        assertDoesNotThrow(() -> {
            botMessage.set(this.messageParser.parse(message));
        });

        assertEquals(expected, botMessage.get());
    }

    @Test
    void createMessage_test() {
        String expected = "PLATFORM:3:PATIENT:5:FEEDBACK_STATUS:6#{bla# bla :bla}";
        String message = this.messageParser.stringify(Sender.PLATFORM,"3", Person.PATIENT, "5", Task.FEEDBACK_STATUS, "6","bla# bla :bla");

        assertEquals(expected, message);
    }

}
