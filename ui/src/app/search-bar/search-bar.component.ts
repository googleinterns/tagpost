import {COMMA, ENTER} from '@angular/cdk/keycodes';
import {Component, OnInit} from '@angular/core';
import {MatChipInputEvent} from '@angular/material/chips';
import {Router} from '@angular/router';

import {DataService} from 'app/service/data.service';

@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.sass']
})

export class SearchBarComponent implements OnInit {

  readonly separatorKeysCodes: number[] = [ENTER, COMMA];
  tags: string[] = [];

  constructor(private dataService: DataService,
              private router: Router) {
  }

  ngOnInit(): void {
  }

  add(event: MatChipInputEvent): void {
    const input = event.input;
    const value = event.value;

    // Add tag
    if ((value || '').trim()) {
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

    if (this.tags.length === 0) {
      this.dataService.clearThreadList();
    }
  }

  search(): void {
    // for now, only first tag entered is used for search
    this.dataService.fetchThreads(this.tags[0]);
    this.router.navigate(['/threads']);
  }
}
