import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ExchangeComponent } from '../list/exchange.component';
import { ExchangeDetailComponent } from '../detail/exchange-detail.component';
import { ExchangeUpdateComponent } from '../update/exchange-update.component';
import { ExchangeRoutingResolveService } from './exchange-routing-resolve.service';

const exchangeRoute: Routes = [
  {
    path: '',
    component: ExchangeComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ExchangeDetailComponent,
    resolve: {
      exchange: ExchangeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ExchangeUpdateComponent,
    resolve: {
      exchange: ExchangeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ExchangeUpdateComponent,
    resolve: {
      exchange: ExchangeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(exchangeRoute)],
  exports: [RouterModule],
})
export class ExchangeRoutingModule {}
