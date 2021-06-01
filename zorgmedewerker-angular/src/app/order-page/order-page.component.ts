import { Component, OnInit } from '@angular/core';
import { Meal } from 'src/model/meal';
import { Allergy } from 'src/model/patient';
import { MessageHandlerService } from '../message-handler.service';

@Component({
  selector: 'app-order-page',
  templateUrl: './order-page.component.html',
  styleUrls: ['./order-page.component.css']
})
export class OrderPageComponent implements OnInit {

  name = '';
  description = '';
  calories = ''
  allergies = '';
  image = '';

  meals: Meal[] = [];

  constructor(private messageHandler: MessageHandlerService) {
    this.messageHandler.getMealEmitter().subscribe((meals) => { this.meals = meals })
  }

  ngOnInit(): void {
  }

  onTest(): void {
    // this.messageHandler.sendMeal({id: '', name: this.name, description: this.description, calories: this.calories, allergies: new Set<Allergy>(), image: this.image});
    this.messageHandler.requestMeals();

    this.name = '';
    this.description = '';
    this.calories = ''
    this.allergies = '';
    this.image = '';
  }

}
