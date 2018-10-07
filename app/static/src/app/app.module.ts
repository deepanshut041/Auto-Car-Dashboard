import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HistoryComponent } from './history/history.component';
import { MapComponent } from './map/map.component';
import { StreamComponent } from './stream/stream.component';
import { VideoComponent } from './video/video.component';
import { AppRoutingModule } from './app-routing.module';

import { AgmCoreModule } from '@agm/core';
import { SplashComponent } from './splash/splash.component';
@NgModule({
  imports: [
    BrowserModule, CommonModule, FormsModule, AgmCoreModule.forRoot({}), AppRoutingModule
  ],
  providers: [],
  declarations: [ AppComponent, HomeComponent, HistoryComponent, MapComponent, StreamComponent, VideoComponent, SplashComponent ],
  bootstrap: [ AppComponent ]
})
export class AppModule {}
