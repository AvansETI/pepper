import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MessageHandlerService } from './message-handler.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  isDarkTheme: boolean = false;
  title = 'zorgmedewerker-angular';
  loggedIn = false;

  constructor(private messageHandler: MessageHandlerService, private router: Router) {
    this.messageHandler.getLoginEmitter().subscribe((authorized) => { this.loggedIn = authorized })
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

  onLogout(): void {
    this.loggedIn = false;
    this.router.navigate(['']);
  }
}
