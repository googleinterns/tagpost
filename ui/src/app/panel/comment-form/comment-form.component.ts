import {COMMA, ENTER} from '@angular/cdk/keycodes';
import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {MatChipInputEvent} from '@angular/material/chips';

import {NgForm} from '@angular/forms';
import {MatSnackBar} from '@angular/material/snack-bar';
import {DataService} from 'app/service/data.service';
import {Comment} from 'compiled_proto/src/proto/tagpost_pb';
import {ActivatedRoute, ParamMap} from '@angular/router';

@Component({
  selector: 'app-comment-form',
  templateUrl: './comment-form.component.html',
  styleUrls: ['./comment-form.component.sass']
})
export class CommentFormComponent implements OnInit {

  @ViewChild('commentForm') form: NgForm;

  threadId: string;
  primaryTag: string;
  name: string;
  extraTags: string[] = [];
  content: string;

  selectable = true;
  removable = true;
  addOnBlur = true;
  readonly separatorKeysCodes: number[] = [ENTER, COMMA];

  constructor(private dataService: DataService,
              private snackBar: MatSnackBar,
              private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe((params: ParamMap) => {
      this.threadId = params.get('id');
      this.primaryTag = params.get('tagName');
    });
  }

  addExtraTags(event: MatChipInputEvent): void {
    const input = event.input;
    const value = event.value;

    // Add tag
    if ((value || '').trim()) {
      this.extraTags.push(value.trim());
    }

    // Reset the input value
    if (input) {
      input.value = '';
    }
  }

  removeTag(tag: string): void {
    const index = this.extraTags.indexOf(tag);

    if (index >= 0) {
      this.extraTags.splice(index, 1);
    }
  }

  /**
   * Add a new comment with form input. Refresh page after adding comment.
   */
  addComment(): void {
    this.dataService.addComment(this.threadId, this.name, this.content, this.primaryTag, this.extraTags)
      .then(comment => {
        this.openSnackBar(comment.getUsername() + 'Your comment is added :D');
        this.refresh();
      })
      .catch(
        err => {
          this.openSnackBar('Sorry, something goes wrong..');
          this.refresh();
        });
  }

  /**
   * Clear all data in the form and refetch comments
   */
  refresh(): void {
    this.form.reset();
    this.extraTags = [];
    this.dataService.fetchComments(this.threadId);
  }

  openSnackBar(message: string): void {
    this.snackBar.open(message, '', { duration: 2000 });
  }
}
