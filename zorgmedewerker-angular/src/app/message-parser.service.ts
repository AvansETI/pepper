import { Injectable } from '@angular/core';
import { Message, Sender, Person, Task } from '../model/message';

@Injectable({
  providedIn: 'root'
})
export class MessageParserService {

  constructor() { }

  parse(message: string): Message {
    let temp = {} as Message;

    const messageSplit: string[] = message.split('#');

    const path: string = messageSplit[0];
    const data: string = messageSplit[1];

    const pathSplit: string[] = path.split(':');

    temp.sender = pathSplit[0] as unknown as Sender;
    temp.senderId = pathSplit[1];
    temp.person = pathSplit[2] as unknown as Person;
    temp.personId = pathSplit[3];
    temp.task = pathSplit[4] as unknown as Task;
    temp.taskId = pathSplit[5];
    temp.data = data.substring(1, data.length - 1);

    return temp;
  }

  stringify(sender: Sender, senderId: string, person: Person, personId: string, task: Task, taskId: string, data: string): string {
    return Sender[sender] + ':'
      + senderId + ':'
      + Person[person] + ':'
      + personId + ':'
      + Task[task] + ':'
      + taskId + '#{'
      + data + '}';
  }

}
