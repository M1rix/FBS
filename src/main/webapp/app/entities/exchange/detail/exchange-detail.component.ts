import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IExchange } from '../exchange.model';

@Component({
  selector: 'jhi-exchange-detail',
  templateUrl: './exchange-detail.component.html',
})
export class ExchangeDetailComponent implements OnInit {
  exchange: IExchange | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ exchange }) => {
      this.exchange = exchange;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
