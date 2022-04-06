import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'profile',
        data: { pageTitle: 'fbsApp.profile.home.title' },
        loadChildren: () => import('./profile/profile.module').then(m => m.ProfileModule),
      },
      {
        path: 'book',
        data: { pageTitle: 'fbsApp.book.home.title' },
        loadChildren: () => import('./book/book.module').then(m => m.BookModule),
      },
      {
        path: 'exchange',
        data: { pageTitle: 'fbsApp.exchange.home.title' },
        loadChildren: () => import('./exchange/exchange.module').then(m => m.ExchangeModule),
      },
      {
        path: 'category',
        data: { pageTitle: 'fbsApp.category.home.title' },
        loadChildren: () => import('./category/category.module').then(m => m.CategoryModule),
      },
      {
        path: 'author',
        data: { pageTitle: 'fbsApp.author.home.title' },
        loadChildren: () => import('./author/author.module').then(m => m.AuthorModule),
      },
      {
        path: 'image',
        data: { pageTitle: 'fbsApp.image.home.title' },
        loadChildren: () => import('./image/image.module').then(m => m.ImageModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
