import { Component, OnInit,ViewChild, ElementRef } from "@angular/core";

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
      lat : 51.678418,
      lng : 7.849007,
      markerUrl:"./../../assets/car.png"
    },
    {
      lat : 51.678426,
      lng : 7.806007,
      markerUrl:"./../../assets/car.png"
    },
    {
      lat : 51.678429,
      lng : 7.854007,
      markerUrl:"./../../assets/car.png"
    },
    {
      lat : 51.68430,
      lng : 7.803007,
      markerUrl:"./../../assets/car.png"
    }
  ]

  constructor(private elementRef:ElementRef) {

  }

  ngOnInit() {
    setInterval(()=>{
      this.markers.map((marker)=>{
        marker.lat +=.0001
        marker.lng +=.0001
      })
    },3000)

  }

  markerClicked(lat,lng){
    console.log(lat +" / "+ lng);
    var modalButton = document.getElementById("modalLauncher");
    modalButton.click();
  }
}
