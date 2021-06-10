import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginPageComponent } from './login-page/login-page.component';
import { DoctorPageComponent } from './doctor-page/doctor-page.component';
import { OrderPageComponent } from './order-page/order-page.component';

const routes: Routes = [ 
  { path: '', component: LoginPageComponent },
  { path: 'patients', component: DoctorPageComponent },
  { path: 'meals', component: OrderPageComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
