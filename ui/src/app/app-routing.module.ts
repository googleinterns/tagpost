import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ThreadDetailComponent} from './panel/thread-detail/thread-detail.component';
import {ThreadListComponent} from './panel/thread-list/thread-list.component';

const routes: Routes = [
  {path: '', redirectTo: '/threads', pathMatch: 'full'},
  {path: 'threads', component: ThreadListComponent},
  {path: 'thread/:id', component: ThreadDetailComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
