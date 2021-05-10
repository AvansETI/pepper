import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.sass']
})
export class LoginPageComponent implements OnInit {

  username: string | undefined;
  password: string | undefined;
  docid: number | undefined;

  constructor() { }

  ngOnInit(): void {
  }

  loginUser()
  {
    if(this.username == "Daphne" && this.password == "daphne" && this.docid == 123)
    {
      console.log("het werkt jaja" + this.username);
    }

  }

}
