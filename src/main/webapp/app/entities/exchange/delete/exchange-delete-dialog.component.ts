import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IExchange } from '../exchange.model';
import { ExchangeService } from '../service/exchange.service';

@Component({
  templateUrl: './exchange-delete-dialog.component.html',
})
export class ExchangeDeleteDialogComponent {
  exchange?: IExchange;

  constructor(protected exchangeService: ExchangeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.exchangeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
