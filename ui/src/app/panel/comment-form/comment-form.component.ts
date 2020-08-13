import {COMMA, ENTER} from '@angular/cdk/keycodes';
import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {NgForm} from '@angular/forms';
import {MatChipInputEvent} from '@angular/material/chips';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ActivatedRoute, ParamMap} from '@angular/router';

import {Subscription} from 'rxjs';

import {DataService} from 'app/service/data.service';

@Component({
  selector: 'app-comment-form',
  templateUrl: './comment-form.component.html',
  styleUrls: ['./comment-form.component.sass']
})
export class CommentFormComponent implements OnInit, OnDestroy {

  @ViewChild('commentForm') form: NgForm;

  threadId: string;
  primaryTag: string;

  // Inputs from comment form
  name: string;
  extraTags: string[] = [];
  content: string;

  readonly separatorKeysCodes: number[] = [ENTER, COMMA];
  private subscription: Subscription;

  constructor(private dataService: DataService,
              private snackBar: MatSnackBar,
              private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.subscription = this.route.paramMap.subscribe((params: ParamMap) => {
      this.threadId = params.get('id');
      this.primaryTag = params.get('tag');
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  addExtraTag(event: MatChipInputEvent): void {
    const input = event.input;
    const value = event.value;

    // Add tag
    if (value.trim()) {
      this.extraTags.push(value.trim());
    }

    // Reset the input value
    if (input) {
      input.value = '';
    }
  }

  removeExtraTag(tag: string): void {
    const index = this.extraTags.indexOf(tag);

    if (index >= 0) {
      this.extraTags.splice(index, 1);
    }
  }

  /**
   * Add a new comment with form input. Refresh page after adding comment.
   */
  addComment(): void {
    this.dataService.addComment(this.threadId, this.name, this.content, this.extraTags)
      .then(comment => {
        this.openSnackBar('Your comment is added :D');
        this.refresh();
      })
      .catch(
        err => {
          this.openSnackBar('Sorry, something went wrong..');
          this.refresh();
        });
  }

  /**
   * Clear all data in the form and refetch comments under current thread.
   */
  refresh(): void {
    this.form.reset();
    this.extraTags = [];
    this.dataService.fetchComments(this.threadId);
  }

  /**
   * A pop up bar to show status(success/err) of adding the new comment.
   */
  openSnackBar(message: string): void {
    this.snackBar.open(message, '', { duration: 2000 });
  }
}
