import {NgModule} from '@angular/core';
import {RouterModule, Routes} from "@angular/router";
import {HomeComponent} from "./home/home.component";
import { StreamComponent } from './stream/stream.component';
import { VideoComponent } from './video/video.component';
import { MapComponent } from './map/map.component';
import { HistoryComponent } from './history/history.component';
import { SplashComponent } from './splash/splash.component';

const routes:Routes = [
    {path: '', component: SplashComponent},
    {path: 'home', component: HomeComponent, children:[
        {path:'stream', component: StreamComponent},
        {path:'video', component: VideoComponent},
        {path:'map', component:MapComponent},
        {path:'history', component:HistoryComponent}
    ]},
]

@NgModule({
    imports:[RouterModule.forRoot(routes)],
    exports:[RouterModule]
})

export class AppRoutingModule{

}