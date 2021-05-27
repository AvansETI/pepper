import { Injectable, EventEmitter } from '@angular/core';
import { Subscription } from 'rxjs';
import { MessageEncryptorService } from './message-encryptor.service';
import { WebSocketService } from './web-socket.service'
import { config } from '../config'
import { MessageParserService } from './message-parser.service';
import { Message, Sender, Person, Task } from '../model/message';
import { Patient, Allergy } from '../model/patient';
import { Meal } from 'src/model/meal';

@Injectable({
  providedIn: 'root'
})
export class MessageHandlerService {

  private webSocketSubscription: Subscription;
  private eventHandler: EventEmitter<Patient[]>;
  private patients: Patient[];
  private questionId = '';
  private mealId = '';

  constructor(private webSocket: WebSocketService, private messageEncryptor: MessageEncryptorService, private messageParser: MessageParserService) {
    this.webSocketSubscription = this.webSocket.getEventHandler().subscribe((message) => this.handle(message));
    this.eventHandler = new EventEmitter<Patient[]>();
    this.patients = [];
  }

  init(): void {
    this.webSocket.connect();
  }

  destroy(): void {
    this.webSocketSubscription.unsubscribe();
    this.webSocket.disconnect();
  }

  async handle(message: string): Promise<void> {
    if (config.backend.encryption.enabled) {
      message = await this.messageEncryptor.decrypt(message, config.backend.encryption.password);
    }

    const staffMessage: Message = this.messageParser.parse(message);

    if (Sender[staffMessage.sender] as unknown as Sender !== Sender.PLATFORM) {
      return;
    }

    this.handleStaffMessage(staffMessage);
  }

  async send(senderId: string, person: Person, personId: string, task: Task, taskId: string, data: string): Promise<void> {
    let message: string = this.messageParser.stringify(Sender.STAFF, senderId, person, personId, task, taskId, data);

    if (config.backend.encryption.enabled) {
      message = await this.messageEncryptor.encrypt(message, config.backend.encryption.password);
    }
    
    this.webSocket.send(message);
  }

  handleStaffMessage(message: Message): void {

    switch(Task[message.task] as unknown as Task) {

      case Task.PATIENT_ID: {

        if (Person[message.person] as unknown as Person === Person.PATIENT) {
          const ids: number[] = JSON.parse(message.data);
        
          ids.forEach((id) => {
            this.send('1', Person.PATIENT, id as unknown as string, Task.PATIENT, '1', '')
          });
        }

        break;
      }
      case Task.PATIENT_NAME: {
        this.addPatient({id: message.personId, name: message.data, birthdate: null, allergies: null});
        break;
      }
      case Task.PATIENT_BIRTHDATE: {
        const date = new Date(0);
        date.setDate(message.data as unknown as number);

        this.addPatient({id: message.personId, name: null, birthdate: date, allergies: null});
        break;
      }
      case Task.PATIENT_ALLERGIES: {
        let allergies: Set<Allergy> = new Set();

        const tempAllergies: string[] = message.data.substring(1, message.data.length - 1).split(', ');
        tempAllergies.forEach((allergy) => {
          allergies.add(allergy as unknown as Allergy);
        })

        this.addPatient({id: message.personId, name: null, birthdate: null, allergies: allergies});
        break;
      }
      case Task.QUESTION_ID: {
        this.questionId = message.taskId;
        break;
      }
      case Task.MEAL_ID: {
        this.mealId = message.taskId;
        break;
      }
      default: {
        console.error('Task not supported');
        break;
      }

    }

  }

  addPatient(patient: Patient): void {
    if (patient.id === null) {
      console.error('Patient id cannot be null')
      return;
    }

    const patientFounded: Patient = this.findPatientById(patient.id);
    const patientFoundedIndex = this.patients.indexOf(patientFounded);

    if (patientFounded === undefined) {
      this.patients.push(patient);
      return;
    }

    if (patient.name !== null) {
      patientFounded.name = patient.name;
    } else if (patient.birthdate !== null) {
      patientFounded.birthdate = patient.birthdate;
    } else if (patient.allergies !== null) {
      patient.allergies.forEach((allergy) => {
        if (patientFounded.allergies === null) {
          patientFounded.allergies = new Set();
        }
        patientFounded.allergies.add(allergy);
      });
    }

    this.patients[patientFoundedIndex] = patientFounded;

    this.eventHandler.emit(this.patients);
  }

  findPatientById(id: string): Patient {
    return this.patients.find((patient) => patient.id === id);
  }

  getEventHandler(): EventEmitter<Patient[]> {
    return this.eventHandler;
  }

  requestPatients(): void {
    this.send('1', Person.PATIENT, '-1', Task.PATIENT_ID, '-1', '');
  }

  async sendQuestionToPatient(patientId: string, question: string): Promise<void> {
    this.questionId = '-1';
    this.send('1', Person.PATIENT, patientId, Task.QUESTION_TEXT, this.questionId, question);

    for (let i = 0; i < 100; i++) {
      await this.sleep(10);
      if (this.questionId !== '-1') {
        break;
      }
    }

    if (this.questionId !== '-1') {
      this.send('1', Person.PATIENT, patientId, Task.QUESTION_TIMESTAMP, this.questionId, (Date.parse(Date()) / 1000).toString());
    }

  }

  async sendMeal(meal: Meal) {
    this.mealId = '-1';
    this.send('1', Person.NONE, '-1', Task.MEAL_NAME, this.mealId, meal.name);

    for (let i = 0; i < 100; i++) {
      await this.sleep(10);
      if (this.mealId !== '-1') {
        break;
      }
    }

    if (this.mealId !== '-1') {
      this.send('1', Person.NONE, '-1', Task.MEAL_DESCRIPTION, this.mealId, meal.description);
      this.send('1', Person.NONE, '-1', Task.MEAL_CALORIES, this.mealId, meal.calories);
      this.send('1', Person.NONE, '-1', Task.MEAL_ALLERGIES, this.mealId, '[]');
      this.send('1', Person.NONE, '-1', Task.MEAL_IMAGE, this.mealId, meal.image);
    }
  }

  async sleep(millis: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, millis));
  }

}
