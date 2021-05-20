import { Component } from '@angular/core';
import { Subscription } from 'rxjs';
import { Person, Task } from '../model/message';
import { Patient } from '../model/patient';
import { MessageHandlerService } from './message-handler.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  isDarkTheme: boolean = false;
  title = 'zorgmedewerker-angular';
  patients: Patient[] = [];

  private patientsSubscription: Subscription;

  constructor(private messageHandler: MessageHandlerService) {
    this.messageHandler.getEventHandler().subscribe((patients) => this.handlePatients(patients));
  }

  ngOnInit(): void {
    this.messageHandler.init();
    this.isDarkTheme = localStorage.getItem('theme') === "Dark";
  }

  ngOnDestroy(): void {
    this.patientsSubscription.unsubscribe();
    this.messageHandler.destroy();
  }

  storeThemeSelection(): void {
    localStorage.setItem('theme', this.isDarkTheme ? "Dark" : "Light");
  }

  test(): void {
    this.messageHandler.send('1', Person.PATIENT, '', Task.PATIENT_ID, '1', '');
  }

  handlePatients(patients: Patient[]): void {
    this.patients = patients;
  }

}
