import { Injectable } from '@angular/core';
import { Subscription } from 'rxjs';
import { MessageEncryptorService } from './message-encryptor.service';
import { WebSocketService } from './web-socket.service'
import { config } from '../config'
import { MessageParserService } from './message-parser.service';
import { Message } from 'src/model/message';
import { Sender } from 'src/model/sender';
import { Person } from 'src/model/person';
import { Task } from 'src/model/task';

@Injectable({
  providedIn: 'root'
})
export class MessageHandlerService {

  private webSocketSubscription: Subscription;

  constructor(private webSocket: WebSocketService, private messageEncryptor: MessageEncryptorService, private messageParser: MessageParserService) {
    this.webSocketSubscription = this.webSocket.getEventHandler().subscribe((message) => { this.handle(message) });
  }

  init() {
    this.webSocket.connect();
  }

  destroy() {
    if (this.webSocketSubscription) {
      this.webSocketSubscription.unsubscribe();
    }
    this.webSocket.disconnect();
  }

  async handle(message: string): Promise<void> {
    if (config.backend.encryption.enabled) {
      message = await this.messageEncryptor.decrypt(message, config.backend.encryption.password);
    }

    const temp: Message = this.messageParser.parse(message);

    console.log(temp)
  }

  async send(senderId: string, person: Person, personId: string, task: Task, taskId: string, data: string): Promise<void> {
    let message: string = this.messageParser.stringify(Sender.STAFF, senderId, person, personId, task, taskId, data);

    console.log(message)

    if (config.backend.encryption.enabled) {
      message = await this.messageEncryptor.encrypt(message, config.backend.encryption.password);
    }
    
    this.webSocket.send(message);
  }

}
