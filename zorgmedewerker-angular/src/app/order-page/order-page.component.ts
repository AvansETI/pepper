import { Component, OnInit } from '@angular/core';
import { Meal } from 'src/model/meal';
import { Allergy } from 'src/model/allergy';
import { MealOrder } from 'src/model/meal-order';
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
  mealOrders: MealOrder[] = [];

  constructor(private messageHandler: MessageHandlerService) {
    this.messageHandler.getMealEmitter().subscribe((meals) => { this.meals = meals })
    this.messageHandler.getMealOrderEmitter().subscribe((mealOrders) => this.mealOrders = mealOrders);
  }

  ngOnInit(): void {
    this.messageHandler.requestMeals();
  }

  formatAllergies(allergies: Set<Allergy>): string {
    return Array.from(allergies).join(', ').toLowerCase()
  }

  onMealSave(): void {
    let temp = new Set<Allergy>()
    temp.add(Allergy.DIABETES)
    temp.add(Allergy.CELERY)
    temp.add(Allergy.EGGS)

    this.messageHandler.sendMeal({id: '-1', name: this.name, description: this.description, calories: this.calories, allergies: temp, image: this.image});
    this.messageHandler.requestMeals();

    this.name = '';
    this.description = '';
    this.calories = ''
    this.allergies = '';
    this.image = '';
  }

}
