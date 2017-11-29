import { Injectable } from "@angular/core";
import { HttpClient } from '@angular/common/http';
import { Http, Headers } from '@angular/http';
import 'rxjs/add/operator/map';
import * as io from 'socket.io-client';
import { Observable } from 'rxjs/Observable';
/**
 * @description
 * @class
 */
@Injectable()
export class FlaskService {
  private socket;
  private location_url = 'http://localhost:5000/socket/location';
  constructor(private http:HttpClient) {
    this.socket = io(this.location_url); 
  }
  getLocation(){
    let observable = new Observable(observer => {
      this.socket.on('location', (data) => {
        observer.next(data);    
      }); 
    })     
    return observable;
  }
  disconnectSocketLocation(){
    this.socket.disconnect();
  }
}
