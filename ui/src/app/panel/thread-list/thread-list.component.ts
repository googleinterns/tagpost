import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {Observable} from 'rxjs';
import {switchMap} from 'rxjs/operators';

import {DataService} from 'app/service/data.service';
import {UtilsService} from 'app/service/utils.service';

@Component({
  templateUrl: './thread-list.component.html',
  styleUrls: ['./thread-list.component.sass']
})
export class ThreadListComponent implements OnInit {
  threadList$: Observable<any>;
  selectedId: number;

  constructor(private dataService: DataService,
              private route: ActivatedRoute,
              private utils: UtilsService) {
  }

  ngOnInit(): void {
    this.threadList$ = this.route.paramMap.pipe(
      switchMap(params => {
        this.selectedId = +params.get('id');
        return this.dataService.threadList;
      })
    );
  }
}
