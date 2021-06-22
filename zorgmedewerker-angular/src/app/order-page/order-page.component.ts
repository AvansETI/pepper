import { Component, OnInit } from '@angular/core';
import { Meal } from 'src/model/meal';
import { Allergy } from 'src/model/allergy';
import { MealOrder } from 'src/model/meal-order';
import { MessageHandlerService } from '../message-handler.service';
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { FormBuilder, FormGroup } from '@angular/forms';

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

  constructor(private messageHandler: MessageHandlerService, private dialog: MatDialog) {
    this.messageHandler.getMealEmitter().subscribe((meals) => { this.meals = meals })
    this.messageHandler.getMealOrderEmitter().subscribe((mealOrders) => this.mealOrders = mealOrders);
  }

  openDialog() {
    this.dialog.open(DialogAddMeal);
  }

  ngOnInit(): void {
    this.messageHandler.requestMeals();
  }

  formatAllergies(allergies: Set<Allergy>): string {
    if (allergies === null) {
      return '';
    }
    return Array.from(allergies).join(', ').toLowerCase()
  }
}

@Component({
  selector: 'app-order-page',
  templateUrl: './order-dialog.html',
})
export class DialogAddMeal {

  name = '';
  description = '';
  calories = ''
  image = '';
  allergies: FormGroup;

  constructor(private messageHandler: MessageHandlerService, fb: FormBuilder, private dialogRef: MatDialogRef<DialogAddMeal>) {
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

  onMealSave(): void {
    if (this.name !== '' && this.description !== '' && this.calories !== '' && this.image !== '') {
      let allergies = new Set<Allergy>()

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

      this.messageHandler.sendMeal({ id: '-1', name: this.name, description: this.description, calories: this.calories, allergies: allergies, image: this.image });
      this.dialogRef.close();
    }

  }
}