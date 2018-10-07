import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-splash',
  templateUrl: './splash.component.html',
  styleUrls: ['./splash.component.css']
})
export class SplashComponent implements OnInit {
  LOGO = "./assets/team.png"
  constructor(private router:Router) { 

  }

  ngOnInit() {
    let msg = document.getElementById("welcomeMessage");
    msg.innerText = "Bravado"
    setTimeout(()=>{
      this.router.navigate(["home/map"])
    }, 5000)
  }

  ngAfterViewInit(){
  }

}
