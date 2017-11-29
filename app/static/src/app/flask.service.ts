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
  private url = 'http://localhost:5000/api/location';
  private socket; 
  constructor(private http:HttpClient) {
    this.socket = io(this.url);
  }
  getLocation(){
    let observable = new Observable(observer => {
      this.socket.on('location', (data) => {
        observer.next(data);    
      }); 
    })     
    return observable;
  }  
}
