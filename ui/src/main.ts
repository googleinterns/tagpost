import {enableProdMode} from '@angular/core';
import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';

import {Thread} from 'compiled_proto/src/proto/tagpost_pb';
import {TagpostServiceClient} from 'compiled_proto/src/proto/Tagpost_rpcServiceClientPb';
import {FetchThreadsByTagRequest} from 'compiled_proto/src/proto/tagpost_rpc_pb';
import {AppModule} from './app/app.module';
import {environment} from './environments/environment';

// Only for demo purpose. Can be removed after we have real protobuf use cases.
const thread = new Thread();
thread.setThreadId('12345');
const serialized = thread.serializeBinary();
console.log(serialized);

if (environment.production) {
  enableProdMode();
}

platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.error(err));

const tagpostService = new TagpostServiceClient('http://localhost:46764');
const fetchThreadsByTagRequest = new FetchThreadsByTagRequest();
fetchThreadsByTagRequest.setTag('noise');
tagpostService.fetchThreadsByTag(fetchThreadsByTagRequest, {}, (err, response) => {
  if (err) {
    console.error(err);
  }
  if (response) {
    console.log(response);
  }
});
