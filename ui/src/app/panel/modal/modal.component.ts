import {Component, OnInit, ViewChild} from '@angular/core';
import {NgModel} from '@angular/forms';
import {Router} from '@angular/router';

import {DataService} from 'app/service/data.service';
import {Thread} from 'compiled_proto/src/proto/tagpost_pb';

/**
 * A Modal component that allow user to enter a tag name string and create a new thread with user specified tag name.
 */
@Component({
  selector: 'app-modal',
  templateUrl: './modal.component.html',
  styleUrls: ['./modal.component.sass']
})
export class ModalComponent implements OnInit {

  @ViewChild('nameForm') form: NgModel;

  isModalActive = false;
  tagName: string;
  topic: string;
  newThread: Thread;
  error: any;

  constructor(private dataService: DataService,
              private router: Router) {
  }

  ngOnInit(): void {
  }

  addThread(): void {
    this.dataService.addThread(this.tagName, this.topic)
      .then(thread => {
        this.newThread = thread;
        this.router.navigate(['threads', thread.getPrimaryTag().getTagName()]);
      })
      .catch(
        err => {
          this.error = err.message;
        });
  }

  /**
   * Activate/deactivate the modal.
   */
  toggleModal(): void {
    this.isModalActive = !this.isModalActive;
    if (!this.isModalActive) {
      this.clearModalData();
    }
  }

  /**
   * Reset all modal value back to default
   */
  private clearModalData(): void {
    this.newThread = undefined;
    this.error = undefined;
    this.topic = undefined;
    this.form.reset();
  }
}
