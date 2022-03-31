import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AuthorComponent } from './list/author.component';
import { AuthorDetailComponent } from './detail/author-detail.component';
import { AuthorUpdateComponent } from './update/author-update.component';
import { AuthorDeleteDialogComponent } from './delete/author-delete-dialog.component';
import { AuthorRoutingModule } from './route/author-routing.module';

@NgModule({
  imports: [SharedModule, AuthorRoutingModule],
  declarations: [AuthorComponent, AuthorDetailComponent, AuthorUpdateComponent, AuthorDeleteDialogComponent],
  entryComponents: [AuthorDeleteDialogComponent],
})
export class AuthorModule {}
