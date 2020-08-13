import {COMMA, ENTER} from '@angular/cdk/keycodes';
import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatChipInputEvent} from '@angular/material/chips';
import {ActivatedRoute, NavigationEnd, Router} from '@angular/router';

import {Subscription} from 'rxjs';
import {filter, map} from 'rxjs/operators';

import {DataService} from 'app/service/data.service';


@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.sass']
})

export class SearchBarComponent implements OnInit, OnDestroy {

  readonly separatorKeysCodes: number[] = [ENTER, COMMA];
  tags: string[] = [];
  subscription: Subscription;

  constructor(private dataService: DataService,
              private router: Router,
              private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    // Access route parameters using streams of NavigationEnd event
    this.subscription = this.router.events.pipe(
      filter((event) => event instanceof NavigationEnd),
      map(() => this.route.firstChild)
    ).subscribe((route: ActivatedRoute) => {
      const tag = route.snapshot.paramMap.get('tag').trim();
      if (tag) {
        this.tags[0] = tag;
      }
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  add(event: MatChipInputEvent): void {
    const input = event.input;
    const value = event.value;

    // Add tag
    if (value.trim()) {
      this.tags.push(value.trim());
    }

    // Reset the input value
    if (input) {
      input.value = '';
    }
  }

  remove(tag: string): void {
    const index = this.tags.indexOf(tag);

    if (index >= 0) {
      this.tags.splice(index, 1);
    }
  }
}
