import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAuthor } from '../author.model';

@Component({
  selector: 'jhi-author-detail',
  templateUrl: './author-detail.component.html',
})
export class AuthorDetailComponent implements OnInit {
  author: IAuthor | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ author }) => {
      this.author = author;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
