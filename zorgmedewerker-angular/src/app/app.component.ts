import { Component } from '@angular/core';
import { MessageHandlerService } from './message-handler.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  isDarkTheme: boolean = false;
  title = 'zorgmedewerker-angular';


  constructor(private messageHandler: MessageHandlerService) {
    
  }

  ngOnInit(): void {
    this.messageHandler.init();
    this.isDarkTheme = localStorage.getItem('theme') === "Dark";
  }

  ngOnDestroy(): void {
    this.messageHandler.destroy();
  }

  storeThemeSelection(): void {
    localStorage.setItem('theme', this.isDarkTheme ? "Dark" : "Light");
  }
}
