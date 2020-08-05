import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';

import {GoogleChartService} from 'app/google-chart/google-chart.service';
import {TagStats} from 'compiled_proto/src/proto/tagpost_pb';

@Component({
  selector: 'app-donut-chart',
  templateUrl: './donut-chart.component.html',
  styleUrls: ['./donut-chart.component.sass']
})
/**
 * A donut chart component to visualize TagStats
 */
export class DonutChartComponent implements OnInit {

  private library;
  @Input() tagStats: TagStats; // Input passed from ThreadDetailComponent
  @ViewChild('donutChart') donutChart: ElementRef;

  constructor(private chartService: GoogleChartService) {
    this.library = this.chartService.getGoogle();
    this.library.charts.load('current', { packages: ['corechart'] });
    this.library.charts.setOnLoadCallback(this.drawChart.bind(this));
  }

  ngOnInit(): void {
  }

  private drawChart(): void {
    const chart = new this.library.visualization.PieChart(this.donutChart.nativeElement);

    const data = new this.library.visualization.DataTable();
    data.addColumn('string', 'Category');
    data.addColumn('number', 'vote');
    data.addRows(this.tagStats.getStatisticsMap().arr_);

    const options = {
      pieHole: 0.4,
      fontSize: 14,
      chartArea: { width: '90%', height: '90%' },
      width: 450,
      height: 400,
      legend: { position: 'left' },
      backgroundColor: '#eef6fc'
    };
    chart.draw(data, options);
  }

}
