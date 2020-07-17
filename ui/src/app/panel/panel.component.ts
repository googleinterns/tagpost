import { Component, OnInit } from '@angular/core';
import {Observable} from 'rxjs';

import {DataService} from '../data.service';

@Component({
  selector: 'app-panel',
  templateUrl: './panel.component.html',
  styleUrls: ['./panel.component.sass']
})
export class PanelComponent implements OnInit {
  threadList$: Observable<any>;

  constructor(private dataService: DataService) {
    this.threadList$ = dataService.threadList;
  }
  ngOnInit(): void {
  }
}
