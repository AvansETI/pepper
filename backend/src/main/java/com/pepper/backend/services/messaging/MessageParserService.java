package com.pepper.backend.services.messaging;

import com.pepper.backend.model.bot.BotMessage;
import com.pepper.backend.model.bot.Person;
import com.pepper.backend.model.bot.Task;
import org.springframework.stereotype.Service;

@Service
public class MessageParserService {

    public BotMessage toBotMessage(String message) throws IndexOutOfBoundsException {
        String[] messageSplit = message.split("#", 2);

        String path = messageSplit[0];
        String data = messageSplit[1];

        String[] pathSplit = path.split(":");

        return BotMessage.builder()
                .botId(pathSplit[1])
                .person(Person.valueOf(pathSplit[2]))
                .personId(pathSplit[3])
                .task(Task.valueOf(pathSplit[4]))
                .data(data.substring(1, data.length() - 1))
                .build();
    }

    public String createMessage(String botId, Person person, String personId, Task task, String data) {
        return "PLATFORM:" + botId + ":" + person.toString() + ":" + personId + ":" + task.toString() + "#" + "{" + data + "}";
    }

}
