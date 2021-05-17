import { Component } from '@angular/core';
import { Subscription } from 'rxjs';
import { WebSocketService } from './web-socket.service'

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  isDarkTheme: boolean = false;
  title = 'zorgmedewerker-angular';

  private webSocketSubscription: Subscription;

  constructor(private webSocket: WebSocketService) {
    this.webSocketSubscription = this.webSocket.getEventHandler().subscribe((message) => this.onMessageReceive(message));
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

  onMessageReceive(message: string): void {
    console.log(message);
  }

  sendMessage(message: string): void {
    this.webSocket.send(message);
  }

}
