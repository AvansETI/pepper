import { Component, OnInit } from '@angular/core';
import { Patient } from 'src/model/patient';
import { Allergy } from 'src/model/allergy';
import { Reminder } from 'src/model/reminder';
import { Feedback } from 'src/model/feedback';
import { Answer } from 'src/model/answer';
import { Question } from 'src/model/question';
import { MessageHandlerService } from 'src/app/message-handler.service';
import { MatDialog } from "@angular/material/dialog";

@Component({
  selector: 'app-doctor-page',
  templateUrl: './doctor-page.component.html',
  styleUrls: ['./doctor-page.component.css']
})
export class DoctorPageComponent implements OnInit {

  patients: Patient[] = [];

  selectedPatient: Patient = null;
  questions: Question[] = [];
  answers: Answer[] = [];
  reminders: Reminder[] = [];
  feedbacks: Feedback[] = [];

  question: string = '';


  constructor(private messageHandler: MessageHandlerService, private dialog: MatDialog) {
    this.messageHandler.getPatientEmitter().subscribe((patients) => { this.patients = patients });
    this.messageHandler.getQuestionEmitter().subscribe((questions) => { this.questions = questions });
    this.messageHandler.getAnswerEmitter().subscribe((answers) => { this.answers = answers });
    this.messageHandler.getReminderEmitter().subscribe((reminders) => { this.reminders = reminders });
    this.messageHandler.getFeedbackEmitter().subscribe((feedbacks) => { this.feedbacks = feedbacks });
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

  openDialog() {

    const dialogRef = this.dialog.open(DialogQuestion);

    dialogRef.afterClosed().subscribe(result => {
      console.log(`Dialog result: ${result}`);
    });
  }

  formatTimestamp(d: Date): string {
    if (d === null) {
      return 'ERROR';
    }
    return ("0" + d.getDate()).slice(-2) + "-" + ("0"+(d.getMonth()+1)).slice(-2) + "-" + d.getFullYear() + " " + ("0" + d.getHours()).slice(-2) + ":" + ("0" + d.getMinutes()).slice(-2);
  }

  formatDate(d: Date): string {
    if (d === null) {
      return 'ERROR';
    }
    return ("0" + d.getDate()).slice(-2) + "-" + ("0"+(d.getMonth()+1)).slice(-2) + "-" + d.getFullYear();
  }

  onTest(patientId: string): void {
    this.messageHandler.requestQuestions(patientId);
    this.messageHandler.requestAnswers(patientId);
    this.messageHandler.requestReminders(patientId);
    this.messageHandler.requestFeedbacks(patientId);
  }

  onSend(patientId: string): void {
    this.messageHandler.sendQuestion({ id: '-1', patientId: patientId, text: this.question, timestamp: new Date() });
    this.question = '';
  }

}

@Component({
  selector: 'app-doctor-page',
  templateUrl: './doctor-dialog-add-patient.html',
})
export class DialogQuestion {

  patients: Patient[] = [];

  selectedPatient: Patient = null;
  questions: Question[] = [];
  answers: Answer[] = [];
  reminders: Reminder[] = [];
  feedbacks: Feedback[] = [];

  question: string = '';
  
  name: string = '';

  constructor(private messageHandler: MessageHandlerService, private dialog: MatDialog) {
    this.messageHandler.getPatientEmitter().subscribe((patients) => { this.patients = patients });
    this.messageHandler.getQuestionEmitter().subscribe((questions) => { this.questions = questions });
    this.messageHandler.getAnswerEmitter().subscribe((answers) => { this.answers = answers });
    this.messageHandler.getReminderEmitter().subscribe((reminders) => { this.reminders = reminders });
    this.messageHandler.getFeedbackEmitter().subscribe((feedbacks) => { this.feedbacks = feedbacks });
  }

  onSend(patientId: string): void {
    this.messageHandler.sendQuestion({ id: '-1', patientId: patientId, text: this.question, timestamp: new Date() });
    this.question = '';
  }
  
  addPatient(){
    this.messageHandler.sendPatient({id: '-1' , name: this.name , birthdate: null, allergies: null});
  }

}
