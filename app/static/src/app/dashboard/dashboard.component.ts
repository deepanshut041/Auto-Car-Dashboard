import { Component, OnInit } from "@angular/core";


@Component({
  selector: "app-dashboard",
  templateUrl: "./dashboard.component.html",
  styleUrls: ["./dashboard.component.css"]
})

export class DashboardComponent implements OnInit {

  lat: number = 51.678418;
  lng: number = 7.809007;

  markers =[
    {
      lat : 51.278418,
      lng : 7.609007,
    },
    {
      lat : 51.58418,
      lng : 7.409007,
    },
    {
      lat : 51.478418,
      lng : 7.609007,
    },
    {
      lat : 51.38418,
      lng : 7.409007,
    }
  ]

  constructor() {

  }

  ngOnInit() {
    console.log(this.markers)
    setInterval(()=>{
      this.markers.map((marker)=>{
        marker.lat +=.01
        marker.lng +=.01
      })
      console.log(this.markers)
    },3000)

  }
}
