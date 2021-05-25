import { Component } from '@angular/core';
import { Person } from 'src/model/person';
import { Task } from 'src/model/task';
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

  ngOnInit() {
    this.messageHandler.init();
    this.isDarkTheme = localStorage.getItem('theme') === "Dark";
  }

  ngOnDestroy() {
    this.messageHandler.destroy();
  }

  storeThemeSelection()
  {
    localStorage.setItem('theme', this.isDarkTheme ? "Dark" : "Light");
  }

  test(): void {
    this.messageHandler.send('1', Person.PATIENT, '2', Task.QUESTION, '1', 'get these bitches');
  }

}
