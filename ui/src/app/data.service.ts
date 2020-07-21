import {Injectable} from '@angular/core';

import {Observable, Subject} from 'rxjs';

import {TagpostServiceClient} from 'compiled_proto/src/proto/Tagpost_rpcServiceClientPb';
import {
  AddThreadWithTagRequest,
  AddThreadWithTagResponse,
  FetchThreadsByTagRequest,
  FetchThreadsByTagResponse
} from 'compiled_proto/src/proto/tagpost_rpc_pb';
import {Tag, Thread} from '../compiled_proto/src/proto/tagpost_pb';
import {environment} from '../environments/environment';

/**
 * A data service that communicate with backend services.
 */
@Injectable({
  providedIn: 'root'
})
export class DataService {
  private client: TagpostServiceClient;

  private threadListSource = new Subject<any>();
  public readonly threadList: Observable<any> = this.threadListSource.asObservable();

  constructor() {
    this.client = new TagpostServiceClient(environment.apiProxy);
  }

  /**
   * Fetch a list of thread with given tag name.
   * If success, Multicast the newly fetched thread list to all threadList observers
   */
  fetchThreads(tag: string): void {
    const req = new FetchThreadsByTagRequest();
    req.setTag(tag);
    this.client.fetchThreadsByTag(req, {}, (err, response: FetchThreadsByTagResponse) => {
      if (err) {
        console.error(err);
      }
      if (response) {
        this.threadListSource.next(response.toObject().threadsList);
      }
    });
  }

  /**
   * Add a new thread with given tag name.
   */
  addThread(tag: string, topic: string): Promise<Thread> {
    return new Promise<any>(((resolve, reject) => {
      const primaryTag = new Tag();
      primaryTag.setTagName(tag);

      const newThread = new Thread();
      newThread.setPrimaryTag(primaryTag);
      newThread.setTopic(topic);

      const req = new AddThreadWithTagRequest();
      req.setThread(newThread);

      this.client.addThreadWithTag(req, {}, (err, response: AddThreadWithTagResponse) => {
        if (err) {
          console.error(err.code, err.message);
          reject(err);
        }
        if (response) {
          resolve(response.toObject().thread);
        }
      });
    }));
  }
}
