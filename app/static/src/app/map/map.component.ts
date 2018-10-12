import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent implements OnInit {
  lat: number = 31.2593; 
  lng: number = 75.7007;
  constructor() { }

  ngOnInit() {
  }

}
