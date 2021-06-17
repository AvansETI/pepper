import { Component, OnInit } from '@angular/core';
import { Patient } from 'src/model/patient';
import { Allergy } from 'src/model/allergy';
import { Reminder } from 'src/model/reminder';
import { Feedback } from 'src/model/feedback';
import { Answer } from 'src/model/answer';
import { Question } from 'src/model/question';
import { MessageHandlerService } from 'src/app/message-handler.service';
import { MatDialog, MatDialogConfig, MatDialogRef } from "@angular/material/dialog";

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

  openPatientDialog(): void {
    const dialogRef = this.dialog.open(DialogAddPatient);

    dialogRef.afterClosed().subscribe(result => {
      console.log(`Dialog result: ${result}`);
    });
  }

  openQuestionDialog(patientId: string): void {
    const dialogRef = this.dialog.open(DialogAddQuestion);

    dialogRef.afterClosed().subscribe(result => {
      this.onQuestionSend(result);
    });
  }

  openReminderDialog(patientId: string): void {
    const dialogRef = this.dialog.open(DialogAddReminder);

    dialogRef.afterClosed().subscribe(result => {
      this.onReminderSend(result);
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

  onRefresh(patientId: string): void {
    this.messageHandler.requestQuestions(patientId);
    this.messageHandler.requestAnswers(patientId);
    this.messageHandler.requestReminders(patientId);
    this.messageHandler.requestFeedbacks(patientId);
  }

  onQuestionSend(question: string): void {
    this.messageHandler.sendQuestion({ id: '-1', patientId: this.selectedPatient.id, text: question, timestamp: new Date() });
  }

  onReminderSend(thing: string): void {
    this.messageHandler.sendReminder({ id: '-1', patientId: this.selectedPatient.id, thing: thing, timestamp: new Date() });
  }

}

@Component({
  selector: 'app-doctor-page',
  templateUrl: './doctor-dialog-add-patient.html',
})
export class DialogAddPatient {

  name: string = '';
  birthdate: string = '';
  allergies: string = '';

  constructor(private messageHandler: MessageHandlerService, private dialog: MatDialog) {

  }
  
  addPatient(): void {
    this.messageHandler.sendPatient({id: '-1' , name: this.name , birthdate: new Date(), allergies: new Set<Allergy>()});
  }

}

@Component({
  selector: 'app-doctor-page',
  templateUrl: './doctor-dialog-add-question.html',
})
export class DialogAddQuestion {

  question: string = '';

  constructor(private dialogRef: MatDialogRef<DialogAddQuestion>) {

  }
  
  onSave(): void {
    if (this.question !== '') {
      this.dialogRef.close(this.question);
    }
  }

}

@Component({
  selector: 'app-doctor-page',
  templateUrl: './doctor-dialog-add-reminder.html',
})
export class DialogAddReminder {

  thing: string = '';

  constructor(private dialogRef: MatDialogRef<DialogAddQuestion>) {

  }
  
  onSave(): void {
    if (this.thing !== '') {
      this.dialogRef.close(this.thing);
    }
  }

}