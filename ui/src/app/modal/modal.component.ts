import {Component, OnInit} from '@angular/core';

import {Thread} from '../../compiled_proto/src/proto/tagpost_pb';
import {DataService} from '../data.service';

/**
 * A Modal component that allow user to enter a tag name string and create a new thread with user specified tag name.
 */
@Component({
  selector: 'app-modal',
  templateUrl: './modal.component.html',
  styleUrls: ['./modal.component.sass']
})
export class ModalComponent implements OnInit {
  isModalActive = false;

  tagName: string;
  newThread: Thread;
  error: any;

  constructor(private dataService: DataService) {
  }

  ngOnInit(): void {
  }

  addThread(): void {
    this.dataService.addThread(this.tagName)
      .then(thread => {
        console.log(thread);
        this.newThread = thread;
      })
      .catch(
        err => {
          console.log(err.message.toString);
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
  clearModalData(): void {
    this.newThread = undefined;
    this.error = undefined;
  }
}
