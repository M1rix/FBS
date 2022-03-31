import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAuthor, Author } from '../author.model';
import { AuthorService } from '../service/author.service';

@Injectable({ providedIn: 'root' })
export class AuthorRoutingResolveService implements Resolve<IAuthor> {
  constructor(protected service: AuthorService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAuthor> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((author: HttpResponse<Author>) => {
          if (author.body) {
            return of(author.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Author());
  }
}
