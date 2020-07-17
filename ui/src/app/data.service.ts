import {Injectable} from '@angular/core';
import {environment} from '../environments/environment';

import {TagpostServiceClient} from 'compiled_proto/src/proto/Tagpost_rpcServiceClientPb';
import {FetchThreadsByTagRequest, FetchThreadsByTagResponse} from 'compiled_proto/src/proto/tagpost_rpc_pb';
import {Observable, Subject} from 'rxjs';

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
}
