import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'home-component',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})

export class HomeComponent implements OnInit {
    lat: number = 51.678418;
    lng: number = 7.809007;
    SPEEDOMETER = "./assets/speedometer.png"

    constructor() {

    }
    
    ngOnInit(): void {
        
    }
  
    
}
