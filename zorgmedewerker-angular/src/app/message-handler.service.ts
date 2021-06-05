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
import { MealOrder } from 'src/model/meal-order';
import { Reminder } from 'src/model/reminder';
import { Feedback } from 'src/model/feedback';
import { Answer } from 'src/model/answer';

@Injectable({
  providedIn: 'root'
})
export class MessageHandlerService {

  // Data subscription
  private webSocketSubscription: Subscription;

  // Event emitters
  private patientEmitter: EventEmitter<Patient[]> = new EventEmitter<Patient[]>();
  private mealEmitter: EventEmitter<Meal[]> = new EventEmitter<Meal[]>();
  private mealOrderEmitter: EventEmitter<MealOrder[]> = new EventEmitter<MealOrder[]>();
  private questionEmitter: EventEmitter<Question[]> = new EventEmitter<Question[]>();
  private answerEmitter: EventEmitter<Answer[]> = new EventEmitter<Answer[]>();
  private reminderEmitter: EventEmitter<Reminder[]> = new EventEmitter<Reminder[]>();
  private feedbackEmitter: EventEmitter<Feedback[]> = new EventEmitter<Feedback[]>();

  // Data containers
  private patients: Patient[] = [];
  private meals: Meal[] = [];
  private mealOrders: MealOrder[] = [];
  private questions: Question[] = [];
  private answers: Answer[] = [];
  private reminders: Reminder[] = [];
  private feedbacks: Feedback[] = [];

  // Post data id's
  private patientPostId = '';
  private mealPostId = '';
  private questionPostId = '';
  private reminderPostId = '';

  // Get data id's
  private patientGetId = '1000';
  private mealGetId = '1001';
  private mealOrderGetId = '1002';
  private questionGetId = '1003';
  private answerGetId = '1004';
  private reminderGetId = '1005';
  private feedbackGetId = '1006';

  constructor(private webSocket: WebSocketService, private messageEncryptor: MessageEncryptorService, private messageParser: MessageParserService) {
    this.webSocketSubscription = this.webSocket.getEventHandler().subscribe((message) => this.handle(message));
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

        if (message.taskId === this.patientGetId) {
          if (Person[message.person] as unknown as Person === Person.PATIENT) {
            const ids: number[] = JSON.parse(message.data);
          
            ids.forEach((id) => {
              this.send('1', Person.PATIENT, id as unknown as string, Task.PATIENT, '1', '');
            });
          }
        } else {
          this.patientPostId = message.taskId;
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
      case Task.MEAL_ID: {

        if (message.taskId === this.mealGetId) {
          const ids: number[] = JSON.parse(message.data);
          
          ids.forEach((id) => {
            this.send('1', Person.NONE, '-1', Task.MEAL, id as unknown as string, '')
          });
        } else {
          this.mealPostId = message.taskId;
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
      case Task.MEAL_ORDER_ID: {
        if (message.taskId === this.mealOrderGetId) {
          const ids: number[] = JSON.parse(message.data);
          
          ids.forEach((id) => {
            this.send('1', Person.NONE, '-1', Task.MEAL_ORDER, id as unknown as string, '');
          });
        }
        break;
      }
      case Task.MEAL_ORDER_MEAL_ID: {
        this.addMealOrder({ id: message.taskId, patientId: message.personId, mealId: message.data, timestamp: null });
        break;
      }
      case Task.MEAL_ORDER_TIMESTAMP: {
        const timestamp = new Date(0);
        timestamp.setSeconds(message.data as unknown as number);

        this.addMealOrder({ id: message.taskId, patientId: message.personId, mealId: null, timestamp: timestamp });
        break;
      }
      case Task.QUESTION_ID: {
        if (message.taskId === this.questionGetId) {
          const ids: number[] = JSON.parse(message.data);
          
          ids.forEach((id) => {
            this.send('1', Person.PATIENT, '-1', Task.QUESTION, id as unknown as string, '');
          });
        } else {
          this.questionPostId = message.taskId;
        }
        break;
      }
      case Task.QUESTION_TEXT: {
        this.addQuestion({ id: message.taskId, patientId: message.personId, text: message.data, timestamp: null });
        break;
      }
      case Task.QUESTION_TIMESTAMP: {
        const timestamp = new Date(0);
        timestamp.setSeconds(message.data as unknown as number);

        this.addQuestion({ id: message.taskId, patientId: message.personId, text: null, timestamp: timestamp });
        break;
      }
      case Task.ANSWER_ID: {
        if (message.taskId === this.answerGetId) {
          const ids: number[] = JSON.parse(message.data);
          
          ids.forEach((id) => {
            this.send('1', Person.PATIENT, '-1', Task.ANSWER, id as unknown as string, '');
          });
        }
        break;
      }
      case Task.ANSWER_QUESTION_ID: {
        this.addAnswer({ id: message.taskId, patientId: message.personId, questionId: message.data, text: null, timestamp: null });
        break;
      }
      case Task.ANSWER_TEXT: {
        this.addAnswer({ id: message.taskId, patientId: message.personId, questionId: null, text: message.data, timestamp: null });
        break;
      }
      case Task.ANSWER_TIMESTAMP: {
        const timestamp = new Date(0);
        timestamp.setSeconds(message.data as unknown as number);

        this.addAnswer({ id: message.taskId, patientId: message.personId, questionId: null, text: null, timestamp: timestamp });
        break;
      }
      case Task.REMINDER_ID: {
        if (message.taskId === this.reminderGetId) {
          const ids: number[] = JSON.parse(message.data);
          
          ids.forEach((id) => {
            this.send('1', Person.PATIENT, '-1', Task.REMINDER, id as unknown as string, '');
          });
        } else {
          this.reminderPostId = message.taskId;
        }
        break;
      }
      case Task.REMINDER_THING: {
        this.addReminder({ id: message.taskId, patientId: message.personId, thing: message.data, timestamp: null });
        break;
      }
      case Task.REMINDER_TIMESTAMP: {
        const timestamp = new Date(0);
        timestamp.setSeconds(message.data as unknown as number);

        this.addReminder({ id: message.taskId, patientId: message.personId, thing: null, timestamp: timestamp });
        break;
      }
      case Task.FEEDBACK_ID: {
        if (message.taskId === this.feedbackGetId) {
          const ids: number[] = JSON.parse(message.data);
          
          ids.forEach((id) => {
            this.send('1', Person.PATIENT, '-1', Task.FEEDBACK, id as unknown as string, '');
          });
        }
        break;
      }
      case Task.FEEDBACK_STATUS: {
        this.addFeedback({ id: message.taskId, patientId: message.personId, status: message.data, explanation: null, timestamp: null });
        break;
      }
      case Task.FEEDBACK_EXPLANATION: {
        this.addFeedback({ id: message.taskId, patientId: message.personId, status: null, explanation: message.data, timestamp: null });
        break;
      }
      case Task.FEEDBACK_TIMESTAMP: {
        const timestamp = new Date(0);
        timestamp.setSeconds(message.data as unknown as number);

        this.addFeedback({ id: message.taskId, patientId: message.personId, status: null, explanation: null, timestamp: timestamp });
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

  addMealOrder(mealOrder: MealOrder): void {
    if (mealOrder.id === null) {
      console.error('Meal order id cannot be null')
      return;
    }

    const mealOrderFounded: MealOrder = this.findMealOrderById(mealOrder.id);
    const mealOrderFoundedIndex = this.mealOrders.indexOf(mealOrderFounded);

    if (mealOrderFounded === undefined) {
      this.mealOrders.push(mealOrder);
      return;
    }

    if (mealOrder.mealId !== null) {
      mealOrderFounded.mealId = mealOrder.mealId;
    } else if (mealOrder.timestamp !== null) {
      mealOrderFounded.timestamp = mealOrder.timestamp;
    }

    this.mealOrders[mealOrderFoundedIndex] = mealOrderFounded;
    this.mealOrderEmitter.emit(this.mealOrders);
  }

  addQuestion(question: Question): void {
    if (question.id === null) {
      console.error('Question id cannot be null')
      return;
    }

    const questionFounded: Question = this.findQuestionById(question.id);
    const questionFoundedIndex = this.questions.indexOf(questionFounded);

    if (questionFounded === undefined) {
      this.questions.push(question);
      return;
    }

    if (question.text !== null) {
      questionFounded.text = question.text;
    } else if (question.timestamp !== null) {
      questionFounded.timestamp = question.timestamp;
    }

    this.questions[questionFoundedIndex] = questionFounded;
    this.questionEmitter.emit(this.questions);
  }

  addAnswer(answer: Answer): void {
    if (answer.id === null) {
      console.error('Answer id cannot be null')
      return;
    }

    const answerFounded: Answer = this.findAnswerById(answer.id);
    const answerFoundedIndex = this.answers.indexOf(answerFounded);

    if (answerFounded === undefined) {
      this.answers.push(answer);
      return;
    }

    if (answer.questionId !== null) {
      answerFounded.questionId = answer.questionId;
    } else if (answer.text !== null) {
      answerFounded.text = answer.text;
    } else if (answer.timestamp !== null) {
      answerFounded.timestamp = answer.timestamp;
    }

    this.answers[answerFoundedIndex] = answerFounded;
    this.answerEmitter.emit(this.answers);
  }

  addReminder(reminder: Reminder): void {
    if (reminder.id === null) {
      console.error('Reminder id cannot be null')
      return;
    }

    const reminderFounded: Reminder = this.findReminderById(reminder.id);
    const reminderFoundedIndex = this.reminders.indexOf(reminderFounded);

    if (reminderFounded === undefined) {
      this.reminders.push(reminder);
      return;
    }

    if (reminder.thing !== null) {
      reminderFounded.thing = reminder.thing;
    } else if (reminder.timestamp !== null) {
      reminderFounded.timestamp = reminder.timestamp;
    }

    this.reminders[reminderFoundedIndex] = reminderFounded;
    this.reminderEmitter.emit(this.reminders);
  }

  addFeedback(feedback: Feedback): void {
    if (feedback.id === null) {
      console.error('Feedback id cannot be null')
      return;
    }

    const feedbackFounded: Feedback = this.findFeedbackById(feedback.id);
    const feedbackFoundedIndex = this.feedbacks.indexOf(feedbackFounded);

    if (feedbackFounded === undefined) {
      this.feedbacks.push(feedback);
      return;
    }

    if (feedback.status !== null) {
      feedbackFounded.status = feedback.status;
    } else if (feedback.explanation !== null) {
      feedbackFounded.explanation = feedback.explanation;
    } else if (feedback.timestamp !== null) {
      feedbackFounded.timestamp = feedback.timestamp;
    }

    this.feedbacks[feedbackFoundedIndex] = feedbackFounded;
    this.feedbackEmitter.emit(this.feedbacks);
  }

  findPatientById(id: string): Patient {
    return this.patients.find((patient) => patient.id === id);
  }

  findMealById(id: string): Meal {
    return this.meals.find((meal) => meal.id === id);
  }

  findMealOrderById(id: string): MealOrder {
    return this.mealOrders.find((mealOrder) => mealOrder.id === id);
  }

  findQuestionById(id: string): Question {
    return this.questions.find((question) => question.id === id);
  }

  findAnswerById(id: string): Answer {
    return this.answers.find((answer) => answer.id === id);
  }

  findReminderById(id: string): Reminder {
    return this.reminders.find((reminder) => reminder.id === id);
  }

  findFeedbackById(id: string): Feedback {
    return this.feedbacks.find((feedback) => feedback.id === id);
  }

  getPatientEmitter(): EventEmitter<Patient[]> {
    return this.patientEmitter;
  }

  getMealEmitter(): EventEmitter<Meal[]> {
    return this.mealEmitter;
  }

  getMealOrderEmitter(): EventEmitter<MealOrder[]> {
    return this.mealOrderEmitter;
  }

  getQuestionEmitter(): EventEmitter<Question[]> {
    return this.questionEmitter;
  }

  getAnswerEmitter(): EventEmitter<Answer[]> {
    return this.answerEmitter;
  }

  getReminderEmitter(): EventEmitter<Reminder[]> {
    return this.reminderEmitter;
  }

  getFeedbackEmitter(): EventEmitter<Feedback[]> {
    return this.feedbackEmitter;
  }

  requestPatients(): void {
    this.patients = [];
    this.send('1', Person.PATIENT, '-1', Task.PATIENT_ID, this.patientGetId, '');
  }

  requestMeals(): void {
    this.meals = [];
    this.mealEmitter.emit(this.meals);
    this.send('1', Person.NONE, '-1', Task.MEAL_ID, this.mealGetId, '')
  }

  requestMealOrders(): void {
    this.mealOrders = [];
    this.mealOrderEmitter.emit(this.mealOrders);
    this.send('1', Person.NONE, '-1', Task.MEAL_ORDER_ID, this.mealOrderGetId, '')
  }

  requestQuestions(patientId: string): void {
    this.questions = [];
    this.questionEmitter.emit(this.questions);
    this.send('1', Person.PATIENT, patientId, Task.QUESTION_ID, this.questionGetId, '')
  }

  requestAnswers(patientId: string): void {
    this.answers = [];
    this.answerEmitter.emit(this.answers);
    this.send('1', Person.PATIENT, patientId, Task.ANSWER_ID, this.answerGetId, '')
  }

  requestReminders(patientId: string): void {
    this.reminders = [];
    this.reminderEmitter.emit(this.reminders);
    this.send('1', Person.PATIENT, patientId, Task.REMINDER_ID, this.reminderGetId, '')
  }

  requestFeedbacks(patientId: string): void {
    this.feedbacks = [];
    this.feedbackEmitter.emit(this.feedbacks);
    this.send('1', Person.PATIENT, patientId, Task.FEEDBACK_ID, this.feedbackGetId, '')
  }

  async sendPatient(patient: Patient) {
    this.patientPostId = '-1';
    this.send('1', Person.PATIENT, patient.id, Task.PATIENT_NAME, this.patientPostId, patient.name);

    for (let i = 0; i < 100; i++) {
      await this.sleep(10);
      if (this.patientPostId !== '-1') {
        break;
      }
    }

    let temp: string[] = [];
    patient.allergies.forEach((allergy) => {
      temp.push(Allergy[allergy])
    })

    if (this.patientPostId !== '-1') {
      this.send('1', Person.PATIENT, patient.id, Task.PATIENT_BIRTHDATE, this.patientPostId, (Date.parse(patient.birthdate.toString()) / 1000).toString());
      this.send('1', Person.PATIENT, patient.id, Task.PATIENT_ALLERGIES, this.patientPostId, `[${Array.from(temp).join(', ')}]`);
    }
  }

  async sendMeal(meal: Meal) {
    this.mealPostId = '-1';
    this.send('1', Person.NONE, '-1', Task.MEAL_NAME, this.mealPostId, meal.name);

    for (let i = 0; i < 100; i++) {
      await this.sleep(10);
      if (this.mealPostId !== '-1') {
        break;
      }
    }

    let temp: string[] = [];
    meal.allergies.forEach((allergy) => {
      temp.push(Allergy[allergy])
    })

    if (this.mealPostId !== '-1') {
      this.send('1', Person.NONE, '-1', Task.MEAL_DESCRIPTION, this.mealPostId, meal.description);
      this.send('1', Person.NONE, '-1', Task.MEAL_CALORIES, this.mealPostId, meal.calories);
      this.send('1', Person.NONE, '-1', Task.MEAL_ALLERGIES, this.mealPostId, `[${Array.from(temp).join(', ')}]`);
      this.send('1', Person.NONE, '-1', Task.MEAL_IMAGE, this.mealPostId, meal.image);
    }
  }

  async sendQuestion(question: Question): Promise<void> {
    this.questionPostId = '-1';
    this.send('1', Person.PATIENT, question.patientId, Task.QUESTION_TEXT, this.questionPostId, question.text);

    for (let i = 0; i < 100; i++) {
      await this.sleep(10);
      if (this.questionPostId !== '-1') {
        break;
      }
    }

    if (this.questionPostId !== '-1') {
      this.send('1', Person.PATIENT, question.patientId, Task.QUESTION_TIMESTAMP, this.questionPostId, (Date.parse(question.timestamp.toString()) / 1000).toString());
    }

  }

  async sendReminder(reminder: Reminder) {
    this.reminderPostId = '-1';
    this.send('1', Person.PATIENT, reminder.patientId, Task.REMINDER_THING, this.reminderPostId, reminder.thing);

    for (let i = 0; i < 100; i++) {
      await this.sleep(10);
      if (this.reminderPostId !== '-1') {
        break;
      }
    }

    if (this.reminderPostId !== '-1') {
      this.send('1', Person.PATIENT, reminder.patientId, Task.REMINDER_TIMESTAMP, this.reminderPostId, (Date.parse(reminder.timestamp.toString()) / 1000).toString());
    }
  }

  async sleep(millis: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, millis));
  }

}
