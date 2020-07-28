import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';

import {Observable} from 'rxjs';
import {switchMap} from 'rxjs/operators';

import {DataService} from '../../service/data.service';
import {UtilsService} from '../../service/utils.service';

@Component({
  selector: 'app-thread-detail',
  templateUrl: './thread-detail.component.html',
  styleUrls: ['./thread-detail.component.sass']
})
/**
 * A component to display all comments under thread and TagStats of primaryTag
 */
export class ThreadDetailComponent implements OnInit {
  commentList$: Observable<any[]>;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private dataService: DataService,
    private utils: UtilsService
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
