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

  error = false;
  errorText = '';

  constructor(private router: Router, private messageHandlerService: MessageHandlerService, private messageEncryptorService: MessageEncryptorService) {
    this.messageHandlerService.getLoginEmitter().subscribe((authorized) => { this.onAuthorized(authorized); })
  }

  ngOnInit(): void {
    
  }

  onLogin(): void {
    if (this.username === '' || this.password === '') {
      this.onError('Gebruikersnaam en/of wachtwoord is niet ingevuld');
      return;
    }
    const hashed = this.messageEncryptorService.hash(this.password);
    this.messageHandlerService.sendUser({ username: this.username, password: hashed });
  }

  onAuthorized(authorized: boolean): void {
    if (authorized) {
      this.reset();
      this.router.navigate(['/patients']);
    } else {
      this.onError('Gebruikersnaam en/of wachtwoord onjuist');
    }
  }

  onError(text: string): void {
    this.error = true;
    this.errorText = text;
  }

  reset(): void {
    this.error = false;
    this.username = '';
    this.password = '';
  }

}
