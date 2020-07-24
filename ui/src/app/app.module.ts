import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {MatChipsModule} from '@angular/material/chips';
import {MatIconModule} from '@angular/material/icon';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

import {IonicModule} from '@ionic/angular';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {GoogleChartModule} from './google-chart/google-chart.module';
import {ModalComponent} from './panel/modal/modal.component';
import {PanelComponent} from './panel/panel.component';
import {ThreadDetailComponent} from './panel/thread-detail/thread-detail.component';
import {ThreadListComponent} from './panel/thread-list/thread-list.component';
import {SearchBarComponent} from './search-bar/search-bar.component';
import {ToolBarComponent} from './tool-bar/tool-bar.component';

@NgModule({
  declarations: [
    AppComponent,
    ModalComponent,
    PanelComponent,
    ThreadDetailComponent,
    ThreadListComponent,
    SearchBarComponent,
    ToolBarComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    BrowserAnimationsModule,
    MatButtonModule,
    MatChipsModule,
    MatIconModule,
    IonicModule.forRoot(),
    GoogleChartModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
