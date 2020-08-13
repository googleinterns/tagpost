import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {Observable} from 'rxjs';
import {switchMap} from 'rxjs/operators';

import {DataService} from 'app/service/data.service';
import {convertTimestamp} from 'app/service/utils';
import {Thread} from 'compiled_proto/src/proto/tagpost_pb';

@Component({
  templateUrl: './thread-list.component.html',
  styleUrls: ['./thread-list.component.sass']
})
export class ThreadListComponent implements OnInit {
  // An observable stream that listen to values multicasted from threadList Subject
  threadList$: Observable<Array<Thread>>;
  convertTimestamp = convertTimestamp;

  constructor(private dataService: DataService,
              private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.threadList$ = this.route.paramMap.pipe(
      switchMap(params => {
        this.dataService.fetchThreads(params.get('tag'));
        return this.dataService.threadList;
      })
    );
  }
}
