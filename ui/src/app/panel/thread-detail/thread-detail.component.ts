import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

import {Observable} from 'rxjs';
import {switchMap} from 'rxjs/operators';

import {DataService} from 'app/service/data.service';
import {convertTimestamp} from 'app/service/utils';
import {Comment, TagStats} from 'compiled_proto/src/proto/tagpost_pb';

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
  tagStats$: Observable<TagStats>;
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
        this.dataService.fetchComments(params.get('id'));
        return this.dataService.commentList;
      })
    );
    this.tagStats$ = this.route.paramMap.pipe(
      switchMap(params => {
        this.dataService.fetchTagStats(params.get('tagName'));
        return this.dataService.tagStats;
      })
    );
  }
}
