import { Component, OnInit } from '@angular/core';
import { MessageEncryptorService } from '../message-encryptor.service';
import { MessageHandlerService } from '../message-handler.service';
import { Router } from "@angular/router"

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.css']
})
export class LoginPageComponent implements OnInit {

  hide = true;
  username = '';
  password = '';

  constructor(private router: Router, private messageHandlerService: MessageHandlerService, private messageEncryptorService: MessageEncryptorService) {
    this.messageHandlerService.getLoginEmitter().subscribe((authorized) => { this.onAuthorized(authorized); })
  }

  ngOnInit(): void {
    
  }

  onLogin(): void {
    const hashed = this.messageEncryptorService.hash(this.password)
    this.messageHandlerService.sendUser({ username: this.username, password: hashed })  
  
    this.username = '';
    this.password = '';
  }

  onAuthorized(authorized: boolean): void {
    if (authorized) {
      this.router.navigate(['/patients']);
    } else {
      console.log('Unauthorized');
    }
  }

}
