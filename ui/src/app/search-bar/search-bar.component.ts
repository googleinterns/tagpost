import {COMMA, ENTER} from '@angular/cdk/keycodes';
import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatChipInputEvent} from '@angular/material/chips';
import {Router} from '@angular/router';

import {Subscription} from 'rxjs';

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
              private router: Router) {
  }

  ngOnInit(): void {
    this.subscription = this.dataService.routeParamTag.subscribe(
      tag => {
        this.tags[0] = tag;
      }
    );
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

  search(): void {
    this.router.navigate(['threads', this.tags[0]]);
  }
}
