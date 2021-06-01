import { Component, OnInit } from '@angular/core';
import { Patient, Allergy } from 'src/model/patient';
import { MessageHandlerService } from 'src/app/message-handler.service';

@Component({
  selector: 'app-doctor-page',
  templateUrl: './doctor-page.component.html',
  styleUrls: ['./doctor-page.component.css']
})
export class DoctorPageComponent implements OnInit {

  patients: Patient[] = [];
  selectedPatient: Patient = null;
  question: string = ''

  constructor(private messageHandler: MessageHandlerService) {
    this.messageHandler.getPatientEmitter().subscribe((patients) => { this.patients = patients });
  }

  ngOnInit(): void {
    this.messageHandler.requestPatients();
  }

  ngAfterContentInit(): void {
    
  }

  onClicked(id: string): void {
    this.selectedPatient = this.patients.find(patient => patient.id === id);
  }

  formatAllergies(allergies: Set<Allergy>): string {
    return Array.from(allergies).join(', ').toLowerCase()
  }

  onSend(id: string): void {
    this.messageHandler.sendQuestionToPatient(id, this.question);
    this.question = '';
  }

}
