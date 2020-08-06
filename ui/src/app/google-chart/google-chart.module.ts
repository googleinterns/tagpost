import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';

import {DonutChartComponent} from 'app/google-chart/donut-chart/donut-chart.component';

@NgModule({
  declarations: [DonutChartComponent],
  imports: [
    CommonModule
  ],
  exports: [
    DonutChartComponent
  ]
})
export class GoogleChartModule {
}
