import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from '@angular/common/http';
/**
 * @description
 * @class
 */
@Injectable()
export class AppService {

  constructor(private http:HttpClient) {
  }
  getSelf(){
    return this.http.get('http://localhost:5000/shutdown');
  }

  emergency(){
    return this.http.get('http://localhost:5000/emergency');   
  }
}