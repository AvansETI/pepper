import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  isDarkTheme: boolean = false;
  title = 'zorgmedewerker-angular';

  ngOnInit()
  {
    this.isDarkTheme = localStorage.getItem('theme') === "Dark" ? true:false
  }

  storeThemeSelection()
  {
    localStorage.setItem('theme', this.isDarkTheme ? "Dark" : "Light")
  }
}
