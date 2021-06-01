import { Component, OnInit } from '@angular/core';
import { Meal } from 'src/model/meal';
import { Allergy } from 'src/model/allergy'
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
    let temp = new Set<Allergy>()
    temp.add(Allergy.DIABETES)
    temp.add(Allergy.CELERY)
    temp.add(Allergy.EGGS)

    this.messageHandler.sendMeal({id: '-1', name: this.name, description: this.description, calories: this.calories, allergies: temp, image: this.image});
    // this.messageHandler.requestMeals();

    this.name = '';
    this.description = '';
    this.calories = ''
    this.allergies = '';
    this.image = '';
  }

}
