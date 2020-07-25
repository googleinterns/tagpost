import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs';

import {DataService} from '../service/data.service';

@Component({
  selector: 'app-panel',
  templateUrl: './panel.component.html',
  styleUrls: ['./panel.component.sass']
})
export class PanelComponent implements OnInit {

  constructor() {
  }

  ngOnInit(): void {
  }
}
