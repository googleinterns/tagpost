import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';
import {switchMap} from 'rxjs/operators';
import {DataService} from '../../service/data.service';

@Component({
  templateUrl: './thread-list.component.html',
  styleUrls: ['./thread-list.component.sass']
})
export class ThreadListComponent implements OnInit {
  threadList$: Observable<any>;
  selectedId: number;

  constructor(private dataService: DataService,
              private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.threadList$ = this.route.paramMap.pipe(
      switchMap(params => {
        this.selectedId = +params.get('id');
        console.log(this.dataService.threadList);
        return this.dataService.threadList;
      })
    );
  }

  /**
   * Convert proto timestamp to readable string
   */
  private convertTimestamp(timestamp: any): string {
    const date = new Date(timestamp.getSeconds() * 1000 + timestamp.getNanos() / 1000);
    const month = date.toLocaleString('default', {month: 'long'});
    return month + ' ' + date.getUTCDate() + ' ' + date.getUTCHours() + ':' + date.getUTCMinutes();
  }
}
