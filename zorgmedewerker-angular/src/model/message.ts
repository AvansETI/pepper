import { Sender } from './sender';
import { Person } from './person';
import { Task } from './task';

export interface Message {

    sender: Sender;
    senderId: string;
    person: Person;
    personId: string;
    task: Task,
    taskId: string;
    data: string;

}
