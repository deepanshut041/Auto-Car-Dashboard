import { Component, OnInit,ViewChild, ElementRef } from "@angular/core";
import { FlaskService } from "../flask.service";

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
      lng : 7.809007
    }
  ]

  constructor(private elementRef:ElementRef,private flaskService:FlaskService) {

  }

  ngOnInit() {
    this.flaskService.getLocation().subscribe((location:any)=>{
      this.markers[0].lat = location.lat
      this.markers[0].lng = location.lon
      this.lat = location.lat
      this.lng = location.lon
      console.log(this.lat + " / " + this.lng)
    }
    )
  }

  markerClicked(lat,lng){
    console.log(lat +" / "+ lng);
    var modalButton = document.getElementById("modalLauncher");
    modalButton.click();
  }
  disconnectServer(){
    this.flaskService.disconnectSocketLocation()
    window.top.close()
  }
}
