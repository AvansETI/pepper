import { Component } from '@angular/core';
import { Subscription } from 'rxjs';
import { MessageEncryptorService } from './message-encryptor.service';
import { WebSocketService } from './web-socket.service'
import { config } from '../config'

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  isDarkTheme: boolean = false;
  title = 'zorgmedewerker-angular';

  private webSocketSubscription: Subscription;

  constructor(private webSocket: WebSocketService, private messageEncryptor: MessageEncryptorService) {
    this.webSocketSubscription = this.webSocket.getEventHandler().subscribe((message) => { this.onMessageReceive(message) });
  }

  ngOnInit() {
    this.webSocket.connect();
    this.isDarkTheme = localStorage.getItem('theme') === "Dark";
  }

  ngOnDestroy() {
    if (this.webSocketSubscription) {
      this.webSocketSubscription.unsubscribe();
    }
    this.webSocket.disconnect();
  }

  storeThemeSelection()
  {
    localStorage.setItem('theme', this.isDarkTheme ? "Dark" : "Light");
  }

  async onMessageReceive(message: string): Promise<void> {
    if (config.backend.encryption.enabled) {
      message = await this.messageEncryptor.decrypt(message, config.backend.encryption.password);
    }
    
    console.log('Received:' + message);
  }

  async sendMessage(message: string): Promise<void> {
    if (config.backend.encryption.enabled) {
      message = await this.messageEncryptor.encrypt(message, config.backend.encryption.password);
    }
    
    this.webSocket.send(message);
    console.log('Send: ' + message);
  }

}
