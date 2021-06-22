import { Component, OnInit } from '@angular/core';
import { Patient } from 'src/model/patient';
import { Allergy } from 'src/model/allergy';
import { Reminder } from 'src/model/reminder';
import { Feedback } from 'src/model/feedback';
import { Answer } from 'src/model/answer';
import { Question } from 'src/model/question';
import { MessageHandlerService } from 'src/app/message-handler.service';
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { FormBuilder, FormGroup } from '@angular/forms';

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
    this.dialog.open(DialogAddPatient);
  }

  openQuestionDialog(): void {
    const dialogRef = this.dialog.open(DialogAddQuestion);

    dialogRef.afterClosed().subscribe(result => {
      if (result !== undefined) {
        this.onQuestionSend(result);
      }
    });
  }

  openReminderDialog(): void {
    const dialogRef = this.dialog.open(DialogAddReminder);

    dialogRef.afterClosed().subscribe(result => {
      if (result !== undefined) {
        this.onReminderSend(result);
      }
    });
  }

  formatTimestamp(d: Date): string {
    if (d === null) {
      return 'ERROR';
    }
    return ("0" + d.getDate()).slice(-2) + "-" + ("0" + (d.getMonth() + 1)).slice(-2) + "-" + d.getFullYear() + " " + ("0" + d.getHours()).slice(-2) + ":" + ("0" + d.getMinutes()).slice(-2);
  }

  formatDate(d: Date): string {
    if (d === null) {
      return 'ERROR';
    }
    return ("0" + d.getDate()).slice(-2) + "-" + ("0" + (d.getMonth() + 1)).slice(-2) + "-" + d.getFullYear();
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
  templateUrl: './doctor-dialog-add-patient.html'
})
export class DialogAddPatient {
  name: string = '';
  birthdate: string = '';

  allergies: FormGroup;

  constructor(private messageHandler: MessageHandlerService, fb: FormBuilder, private dialogRef: MatDialogRef<DialogAddPatient>) {
    this.allergies = fb.group({
      gluten: false,
      diabetes: false,
      lactose: false,
      eggs: false,
      celery: false,
      nuts: false,
      soy: false,
      wheat: false,
      fish: false,
      shellfish: false
    });
  }

  addPatient(): void {
    if (this.name !== '' && this.birthdate !== '') {
      let allergies = new Set<Allergy>();

      if (this.allergies.value.gluten) {
        allergies.add(Allergy.GLUTEN);
      }

      if (this.allergies.value.diabetes) {
        allergies.add(Allergy.DIABETES);
      }

      if (this.allergies.value.lactose) {
        allergies.add(Allergy.LACTOSE);
      }

      if (this.allergies.value.eggs) {
        allergies.add(Allergy.EGGS);
      }

      if (this.allergies.value.celery) {
        allergies.add(Allergy.CELERY);
      }

      if (this.allergies.value.nuts) {
        allergies.add(Allergy.NUTS);
      }

      if (this.allergies.value.soy) {
        allergies.add(Allergy.SOY);
      }

      if (this.allergies.value.wheat) {
        allergies.add(Allergy.WHEAT);
      }

      if (this.allergies.value.fish) {
        allergies.add(Allergy.FISH);
      }

      if (this.allergies.value.shellfish) {
        allergies.add(Allergy.SHELLFISH);
      }

      this.messageHandler.sendPatient({ id: '-1', name: this.name, birthdate: new Date(this.birthdate), allergies: allergies });
      this.dialogRef.close();
    }
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

  constructor(private dialogRef: MatDialogRef<DialogAddReminder>) {

  }

  onSave(): void {
    if (this.thing !== '') {
      this.dialogRef.close(this.thing);
    }
  }

}