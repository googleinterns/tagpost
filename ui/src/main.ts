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

function createGrpcClient(): TagpostServiceClient {
  let url = new URL('/', window.location.toString()).toString();
  if (url.endsWith('/')) {
    url = url.substring(0, url.length - 1);
  }
  return new TagpostServiceClient(url);
}

const tagpostService = createGrpcClient();
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
