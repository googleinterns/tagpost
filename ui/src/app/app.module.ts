import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {MatChipsModule} from '@angular/material/chips';
import {MatIconModule} from '@angular/material/icon';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

import {IonicModule} from '@ionic/angular';

import {AppRoutingModule} from 'app/app-routing.module';
import {AppComponent} from 'app/app.component';
import {GoogleChartModule} from 'app/google-chart/google-chart.module';
import {ModalComponent} from 'app/panel/modal/modal.component';
import {PanelComponent} from 'app/panel/panel.component';
import {ThreadDetailComponent} from 'app/panel/thread-detail/thread-detail.component';
import {ThreadListComponent} from 'app/panel/thread-list/thread-list.component';
import {SearchBarComponent} from 'app/search-bar/search-bar.component';
import {ToolBarComponent} from 'app/tool-bar/tool-bar.component';

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
