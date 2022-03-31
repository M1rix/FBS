import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProfile, Profile } from '../profile.model';
import { ProfileService } from '../service/profile.service';

@Injectable({ providedIn: 'root' })
export class ProfileRoutingResolveService implements Resolve<IProfile> {
  constructor(protected service: ProfileService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProfile> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((profile: HttpResponse<Profile>) => {
          if (profile.body) {
            return of(profile.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Profile());
  }
}
