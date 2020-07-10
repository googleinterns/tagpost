import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';
import { environment } from './environments/environment';
import { Thread } from "compiled_proto/src/proto/tagpost_pb";

// Only for demo purpose. Can be removed after we have real protobuf use cases.
const thread = new Thread();
thread.setThreadId("12345");
const serialized = thread.serializeBinary();
console.log(serialized);

if (environment.production) {
  enableProdMode();
}

platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.error(err));
