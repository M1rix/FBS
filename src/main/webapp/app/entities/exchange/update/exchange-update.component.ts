import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IExchange, Exchange } from '../exchange.model';
import { ExchangeService } from '../service/exchange.service';
import { IProfile } from 'app/entities/profile/profile.model';
import { ProfileService } from 'app/entities/profile/service/profile.service';
import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';

@Component({
  selector: 'jhi-exchange-update',
  templateUrl: './exchange-update.component.html',
})
export class ExchangeUpdateComponent implements OnInit {
  isSaving = false;

  formProfilesCollection: IProfile[] = [];
  toProfilesCollection: IProfile[] = [];
  booksCollection: IBook[] = [];

  editForm = this.fb.group({
    id: [],
    createdBy: [null, [Validators.required, Validators.maxLength(50)]],
    createdDate: [],
    lastModifiedBy: [null, [Validators.maxLength(50)]],
    lastModifiedDate: [],
    formProfile: [],
    toProfile: [],
    book: [],
  });

  constructor(
    protected exchangeService: ExchangeService,
    protected profileService: ProfileService,
    protected bookService: BookService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ exchange }) => {
      if (exchange.id === undefined) {
        const today = dayjs().startOf('day');
        exchange.createdDate = today;
        exchange.lastModifiedDate = today;
      }

      this.updateForm(exchange);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const exchange = this.createFromForm();
    if (exchange.id !== undefined) {
      this.subscribeToSaveResponse(this.exchangeService.update(exchange));
    } else {
      this.subscribeToSaveResponse(this.exchangeService.create(exchange));
    }
  }

  trackProfileById(index: number, item: IProfile): number {
    return item.id!;
  }

  trackBookById(index: number, item: IBook): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IExchange>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(exchange: IExchange): void {
    this.editForm.patchValue({
      id: exchange.id,
      createdBy: exchange.createdBy,
      createdDate: exchange.createdDate ? exchange.createdDate.format(DATE_TIME_FORMAT) : null,
      lastModifiedBy: exchange.lastModifiedBy,
      lastModifiedDate: exchange.lastModifiedDate ? exchange.lastModifiedDate.format(DATE_TIME_FORMAT) : null,
      formProfile: exchange.formProfile,
      toProfile: exchange.toProfile,
      book: exchange.book,
    });

    this.formProfilesCollection = this.profileService.addProfileToCollectionIfMissing(this.formProfilesCollection, exchange.formProfile);
    this.toProfilesCollection = this.profileService.addProfileToCollectionIfMissing(this.toProfilesCollection, exchange.toProfile);
    this.booksCollection = this.bookService.addBookToCollectionIfMissing(this.booksCollection, exchange.book);
  }

  protected loadRelationshipsOptions(): void {
    this.profileService
      .query({ 'exchangeId.specified': 'false' })
      .pipe(map((res: HttpResponse<IProfile[]>) => res.body ?? []))
      .pipe(
        map((profiles: IProfile[]) =>
          this.profileService.addProfileToCollectionIfMissing(profiles, this.editForm.get('formProfile')!.value)
        )
      )
      .subscribe((profiles: IProfile[]) => (this.formProfilesCollection = profiles));

    this.profileService
      .query({ 'exchangeId.specified': 'false' })
      .pipe(map((res: HttpResponse<IProfile[]>) => res.body ?? []))
      .pipe(
        map((profiles: IProfile[]) => this.profileService.addProfileToCollectionIfMissing(profiles, this.editForm.get('toProfile')!.value))
      )
      .subscribe((profiles: IProfile[]) => (this.toProfilesCollection = profiles));

    this.bookService
      .query({ 'exchangeId.specified': 'false' })
      .pipe(map((res: HttpResponse<IBook[]>) => res.body ?? []))
      .pipe(map((books: IBook[]) => this.bookService.addBookToCollectionIfMissing(books, this.editForm.get('book')!.value)))
      .subscribe((books: IBook[]) => (this.booksCollection = books));
  }

  protected createFromForm(): IExchange {
    return {
      ...new Exchange(),
      id: this.editForm.get(['id'])!.value,
      createdBy: this.editForm.get(['createdBy'])!.value,
      createdDate: this.editForm.get(['createdDate'])!.value
        ? dayjs(this.editForm.get(['createdDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      lastModifiedBy: this.editForm.get(['lastModifiedBy'])!.value,
      lastModifiedDate: this.editForm.get(['lastModifiedDate'])!.value
        ? dayjs(this.editForm.get(['lastModifiedDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      formProfile: this.editForm.get(['formProfile'])!.value,
      toProfile: this.editForm.get(['toProfile'])!.value,
      book: this.editForm.get(['book'])!.value,
    };
  }
}
