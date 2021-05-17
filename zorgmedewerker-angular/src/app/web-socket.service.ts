import { Injectable, EventEmitter } from '@angular/core';
import * as SockJS from 'sockjs-client';
import Stomp, { Client } from 'webstomp-client';
import { config } from '../config';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  private client: Client;
  private eventHandler: EventEmitter<string>;

  constructor() {
    this.client = Stomp.over(new SockJS('http://' + config.backend.auth.username + ':' + config.backend.auth.password + '@' + config.backend.host + '/pepper'));
    this.eventHandler = new EventEmitter<string>();
  }

  connect(): void {
    this.client.connect(
      {
        login: config.backend.auth.username,
        passcode: config.backend.auth.password,
        host: config.backend.host
      },
      () => {
        this.client.subscribe('/topic/data', (message) => {
          if (message.body) {
            this.eventHandler.emit(message.body);
          }
        });
      },
      (error) => {
        console.log(error);
      }
    );
  }

  disconnect(): void {
    if (this.client) {
      this.client.disconnect();
    }
  }

  send(payload: string): void {
    if (this.client && this.client.connected) {
      this.client.send('/app/data', payload, {});
      console.log('send to backend: ' + payload)
    }
  }

  getEventHandler(): EventEmitter<string> {
    return this.eventHandler;
  }

}
