package com.pepper.backend.services.messaging;

import com.pepper.backend.model.messaging.Message;
import com.pepper.backend.model.messaging.Person;
import com.pepper.backend.model.messaging.Sender;
import com.pepper.backend.model.messaging.Task;
import org.springframework.stereotype.Service;

@Service
public class MessageParserService {

    public Message parse(String message) throws IndexOutOfBoundsException {
        String[] messageSplit = message.split("#", 2);

        String path = messageSplit[0];
        String data = messageSplit[1];

        String[] pathSplit = path.split(":");

        return Message.builder()
                .sender(Sender.valueOf(pathSplit[0]))
                .senderId(pathSplit[1])
                .person(Person.valueOf(pathSplit[2]))
                .personId(pathSplit[3])
                .task(Task.valueOf(pathSplit[4]))
                .taskId(pathSplit[5])
                .data(data.substring(1, data.length() - 1))
                .build();
    }

    public String stringify(Sender sender, String senderId, Person person, String personId, Task task, String taskId, String data) {
        return sender.toString() + ":"
                + senderId + ":"
                + person.toString() + ":"
                + personId + ":"
                + task.toString() + ":"
                + taskId + "#{"
                + data + "}";
    }

}
