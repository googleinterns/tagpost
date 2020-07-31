import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

import {Observable} from 'rxjs';
import {switchMap} from 'rxjs/operators';

import {DataService} from 'app/service/data.service';
import {convertTimestamp} from 'app/service/utils';
import {Comment} from 'compiled_proto/src/proto/tagpost_pb';

@Component({
  selector: 'app-thread-detail',
  templateUrl: './thread-detail.component.html',
  styleUrls: ['./thread-detail.component.sass']
})
/**
 * A component to display all comments under thread and TagStats of primaryTag
 */
export class ThreadDetailComponent implements OnInit {
  commentList$: Observable<Array<Comment>>;
  convertTimestamp = convertTimestamp;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private dataService: DataService
  ) {
  }

  ngOnInit(): void {
    this.commentList$ = this.route.paramMap.pipe(
      switchMap(params => {
        this.dataService.fetchComments(this.route.snapshot.params.id);
        return this.dataService.commentList;
      })
    );
  }
}