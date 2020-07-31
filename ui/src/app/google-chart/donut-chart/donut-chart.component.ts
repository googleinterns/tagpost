import {Component, OnInit} from '@angular/core';

import {GoogleChartService} from 'app/google-chart/google-chart.service';

@Component({
  selector: 'app-donut-chart',
  templateUrl: './donut-chart.component.html',
  styleUrls: ['./donut-chart.component.sass']
})
export class DonutChartComponent implements OnInit {

  private library: any;

  constructor(private chartService: GoogleChartService) {
    this.library = this.chartService.getGoogle();
    this.library.charts.load('current', { packages: ['corechart'] });
    this.library.charts.setOnLoadCallback(this.drawChart.bind(this));
  }

  ngOnInit(): void {
  }

  private drawChart(): void {
    const chart = new this.library.visualization.PieChart(document.getElementById('donutchart'));
    const data = new this.library.visualization.arrayToDataTable([
      ['Category', 'Vote'],
      ['noise', 20],
      ['other', 2],
      ['undefined', 1],
    ]);

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
