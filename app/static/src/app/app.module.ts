import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from "./app-routing.module";
import { AgmCoreModule } from '@agm/core';

import { AppComponent } from './app.component';
import { LoginComponent } from "./login/login.component";
import { DashboardComponent } from "./dashboard/dashboard.component";

@NgModule({
  declarations: [
    AppComponent, LoginComponent, DashboardComponent
  ],
  imports: [
    BrowserModule, AppRoutingModule,AgmCoreModule.forRoot({
      apiKey: 'AIzaSyAcXrL1ag0b0f0CW5rQ_dGyh5ZhsMNTUc0'
    })
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
