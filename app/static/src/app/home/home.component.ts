import { Component, OnInit } from '@angular/core';
import { AppService } from "../app.service";

@Component({
  selector: 'home-component',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})

export class HomeComponent implements OnInit {
  SPEEDOMETER = "./assets/speedometer.png"
  constructor(private appservice: AppService) {

  }

  ngOnInit(): void {

  }

  shutdown(): void {
    this.appservice.getSelf().subscribe(
      (response: any) => {
        console.log(response)
      }, (err) => {
        console.log(err)
      }
    )
  }

  emergency(): void {
    this.appservice.emergency().subscribe(
      (response: any) => {
        console.log(response)
      }, (err) => {
        console.log(err)
      }
    )
  }
}
