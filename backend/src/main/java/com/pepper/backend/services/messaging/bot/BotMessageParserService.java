package com.pepper.backend.services.messaging.bot;

import com.pepper.backend.model.messaging.bot.BotMessage;
import com.pepper.backend.model.messaging.bot.Person;
import com.pepper.backend.model.messaging.bot.Sender;
import com.pepper.backend.model.messaging.bot.Task;
import org.springframework.stereotype.Service;

@Service
public class BotMessageParserService {

    public BotMessage toBotMessage(String message) throws IndexOutOfBoundsException {
        String[] messageSplit = message.split("#", 2);

        String path = messageSplit[0];
        String data = messageSplit[1];

        String[] pathSplit = path.split(":");

        return BotMessage.builder()
                .sender(Sender.valueOf(pathSplit[0]))
                .senderId(pathSplit[1])
                .person(Person.valueOf(pathSplit[2]))
                .personId(pathSplit[3])
                .task(Task.valueOf(pathSplit[4]))
                .taskId(pathSplit[5])
                .data(data.substring(1, data.length() - 1))
                .build();
    }

    public String createMessage(Sender sender, String botId, Person person, String personId, Task task, String taskId, String data) {
        return sender.toString() + ":"
                + botId + ":"
                + person.toString() + ":"
                + personId + ":"
                + task.toString() + ":"
                + taskId + "#{"
                + data + "}";
    }

}
