import { Injectable, EventEmitter } from '@angular/core';
import { Subscription } from 'rxjs';
import { MessageEncryptorService } from './message-encryptor.service';
import { WebSocketService } from './web-socket.service'
import { config } from '../config'
import { MessageParserService } from './message-parser.service';
import { Message, Sender, Person, Task } from 'src/model/message';
import { Patient } from 'src/model/patient';
import { Allergy } from 'src/model/allergy';
import { Question } from 'src/model/question';
import { Meal } from 'src/model/meal';

@Injectable({
  providedIn: 'root'
})
export class MessageHandlerService {

  private webSocketSubscription: Subscription;
  private patientEmitter: EventEmitter<Patient[]>;
  private mealEmitter: EventEmitter<Meal[]>;

  private patients: Patient[] = [];
  private meals: Meal[] = [];

  private questionId = '';
  private mealId = '';

  private patientRequestId = '1000';
  private mealRequestId = '1001';
  private mealOrderRequestId = '1002';

  constructor(private webSocket: WebSocketService, private messageEncryptor: MessageEncryptorService, private messageParser: MessageParserService) {
    this.webSocketSubscription = this.webSocket.getEventHandler().subscribe((message) => this.handle(message));
    this.patientEmitter = new EventEmitter<Patient[]>();
    this.mealEmitter = new EventEmitter<Meal[]>();
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

        if (message.taskId === this.patientRequestId) {
          if (Person[message.person] as unknown as Person === Person.PATIENT) {
            const ids: number[] = JSON.parse(message.data);
          
            ids.forEach((id) => {
              this.send('1', Person.PATIENT, id as unknown as string, Task.PATIENT, '1', '')
            });
          }
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
        const tempAllergies: string[] = message.data.substring(1, message.data.length - 1).replace(' ', '').split(',');
        tempAllergies.forEach((allergy) => {
          if (allergy !== '') {
            allergies.add(allergy as unknown as Allergy);
          }
        })

        this.addPatient({id: message.personId, name: null, birthdate: null, allergies: allergies});
        break;
      }
      case Task.QUESTION_ID: {
        this.questionId = message.taskId;
        break;
      }
      case Task.MEAL_ID: {

        if (message.taskId === this.mealRequestId) {
          const ids: number[] = JSON.parse(message.data);
          
          ids.forEach((id) => {
            this.send('1', Person.NONE, "-1", Task.MEAL, id as unknown as string, '')
          });
        } else {
          this.mealId = message.taskId;
        }
        
        break;
      }
      case Task.MEAL_NAME: {
        this.addMeal({ id: message.taskId, name: message.data, description: null, calories: null, allergies: null, image: null });
        break;
      }
      case Task.MEAL_DESCRIPTION: {
        this.addMeal({ id: message.taskId, name: null, description: message.data, calories: null, allergies: null, image: null });
        break;
      }
      case Task.MEAL_CALORIES: {
        this.addMeal({ id: message.taskId, name: null, description: null, calories: message.data, allergies: null, image: null });
        break;
      }
      case Task.MEAL_ALLERGIES: {
        let allergies: Set<Allergy> = new Set();

        const tempAllergies: string[] = message.data.substring(1, message.data.length - 1).replace(' ', '').split(',');
        tempAllergies.forEach((allergy) => {
          if (allergy !== '') {
            allergies.add(allergy as unknown as Allergy);
          }
        })

        this.addMeal({ id: message.taskId, name: null, description: null, calories: null, allergies: allergies, image: null });
        break;
      }
      case Task.MEAL_IMAGE: {
        this.addMeal({ id: message.taskId, name: null, description: null, calories: null, allergies: null, image: message.data });
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
      if (patientFounded.allergies === null) {
        patientFounded.allergies = new Set();
      }

      patient.allergies.forEach((allergy) => {
        patientFounded.allergies.add(allergy);
      });
    }

    this.patients[patientFoundedIndex] = patientFounded;

    this.patientEmitter.emit(this.patients);
  }

  addMeal(meal: Meal): void {
    if (meal.id === null) {
      console.error('Meal id cannot be null')
      return;
    }

    const mealFounded: Meal = this.findMealById(meal.id);
    const mealFoundedIndex = this.meals.indexOf(mealFounded);

    if (mealFounded === undefined) {
      this.meals.push(meal);
      return;
    }

    if (meal.name !== null) {
      mealFounded.name = meal.name;
    } else if (meal.description !== null) {
      mealFounded.description = meal.description;
    } else if (meal.calories !== null) {
      mealFounded.calories = meal.calories;
    } else if (meal.allergies !== null) {
      if (mealFounded.allergies === null) {
        mealFounded.allergies = new Set();
      }
      
      meal.allergies.forEach((allergy) => {
        mealFounded.allergies.add(allergy);
      })
    } else if (meal.image !== null) {
      mealFounded.image = meal.image;
    }

    this.meals[mealFoundedIndex] = mealFounded;

    this.mealEmitter.emit(this.meals);
  }

  findPatientById(id: string): Patient {
    return this.patients.find((patient) => patient.id === id);
  }

  findMealById(id: string): Meal {
    return this.meals.find((meal) => meal.id === id);
  }

  getPatientEmitter(): EventEmitter<Patient[]> {
    return this.patientEmitter;
  }

  getMealEmitter(): EventEmitter<Meal[]> {
    return this.mealEmitter;
  }

  requestPatients(): void {
    this.send('1', Person.PATIENT, '-1', Task.PATIENT_ID, this.patientRequestId, '');
  }

  requestMeals(): void {
    this.send('1', Person.NONE, '-1', Task.MEAL_ID, this.mealRequestId, '')
  }

  async sendQuestion(question: Question): Promise<void> {
    this.questionId = '-1';
    this.send('1', Person.PATIENT, question.patientId, Task.QUESTION_TEXT, this.questionId, question.text);

    for (let i = 0; i < 100; i++) {
      await this.sleep(10);
      if (this.questionId !== '-1') {
        break;
      }
    }

    if (this.questionId !== '-1') {
      this.send('1', Person.PATIENT, question.patientId, Task.QUESTION_TIMESTAMP, this.questionId, (Date.parse(question.timestamp.toString()) / 1000).toString());
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

    let temp: string[] = [];
    meal.allergies.forEach((allergy) => {
      temp.push(Allergy[allergy])
    })

    if (this.mealId !== '-1') {
      this.send('1', Person.NONE, '-1', Task.MEAL_DESCRIPTION, this.mealId, meal.description);
      this.send('1', Person.NONE, '-1', Task.MEAL_CALORIES, this.mealId, meal.calories);
      this.send('1', Person.NONE, '-1', Task.MEAL_ALLERGIES, this.mealId, `[${Array.from(temp).join(', ')}]`);
      this.send('1', Person.NONE, '-1', Task.MEAL_IMAGE, this.mealId, meal.image);
    }
  }

  async sleep(millis: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, millis));
  }

}
