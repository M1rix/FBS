import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IExchange, Exchange } from '../exchange.model';
import { ExchangeService } from '../service/exchange.service';

@Injectable({ providedIn: 'root' })
export class ExchangeRoutingResolveService implements Resolve<IExchange> {
  constructor(protected service: ExchangeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IExchange> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((exchange: HttpResponse<Exchange>) => {
          if (exchange.body) {
            return of(exchange.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Exchange());
  }
}
