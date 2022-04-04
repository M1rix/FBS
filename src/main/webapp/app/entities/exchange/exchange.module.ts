import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ExchangeComponent } from './list/exchange.component';
import { ExchangeDetailComponent } from './detail/exchange-detail.component';
import { ExchangeUpdateComponent } from './update/exchange-update.component';
import { ExchangeDeleteDialogComponent } from './delete/exchange-delete-dialog.component';
import { ExchangeRoutingModule } from './route/exchange-routing.module';

@NgModule({
  imports: [SharedModule, ExchangeRoutingModule],
  declarations: [ExchangeComponent, ExchangeDetailComponent, ExchangeUpdateComponent, ExchangeDeleteDialogComponent],
  entryComponents: [ExchangeDeleteDialogComponent],
})
export class ExchangeModule {}
