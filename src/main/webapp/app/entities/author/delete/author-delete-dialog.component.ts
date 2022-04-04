import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAuthor } from '../author.model';
import { AuthorService } from '../service/author.service';

@Component({
  templateUrl: './author-delete-dialog.component.html',
})
export class AuthorDeleteDialogComponent {
  author?: IAuthor;

  constructor(protected authorService: AuthorService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.authorService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
