import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ThreadDetailComponent} from 'app/panel/thread-detail/thread-detail.component';
import {ThreadListComponent} from 'app/panel/thread-list/thread-list.component';
import {WelcomeComponent} from './panel/welcome/welcome.component';

const routes: Routes = [
  {path: '', component: WelcomeComponent},
  {path: 'threads/:tag', component: ThreadListComponent},
  {path: 'threads/:tag/:id', component: ThreadDetailComponent},
  {path: '**', redirectTo: ''}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
