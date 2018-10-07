import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  
  toggleFullScreen() {
    let elem =  document.body; 
    let methodToBeInvoked = elem.requestFullscreen || 
     elem.webkitRequestFullScreen || elem['mozRequestFullscreen'] || 
     elem['msRequestFullscreen']; 
    if(methodToBeInvoked) methodToBeInvoked.call(elem);
  }

  ngOnInit(): void {
    this.toggleFullScreen();
  }
  //Called after the constructor, initializing input properties, and the first call to ngOnChanges.
  //Add 'implements OnInit' to the class.
  
} 
