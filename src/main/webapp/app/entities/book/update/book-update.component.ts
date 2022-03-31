import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IBook, Book } from '../book.model';
import { BookService } from '../service/book.service';
import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';
import { BookStatus } from 'app/entities/enumerations/book-status.model';

@Component({
  selector: 'jhi-book-update',
  templateUrl: './book-update.component.html',
})
export class BookUpdateComponent implements OnInit {
  isSaving = false;
  bookStatusValues = Object.keys(BookStatus);

  categoriesSharedCollection: ICategory[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.maxLength(255)]],
    imageUrl: [null, [Validators.required, Validators.maxLength(255)]],
    pages: [null, [Validators.min(0)]],
    status: [null, [Validators.required]],
    likes: [null, [Validators.min(0)]],
    createdBy: [null, [Validators.required, Validators.maxLength(50)]],
    createdDate: [null, [Validators.required]],
    lastModifiedBy: [null, [Validators.maxLength(50)]],
    lastModifiedDate: [],
    category: [],
  });

  constructor(
    protected bookService: BookService,
    protected categoryService: CategoryService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ book }) => {
      if (book.id === undefined) {
        const today = dayjs().startOf('day');
        book.createdDate = today;
        book.lastModifiedDate = today;
      }

      this.updateForm(book);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const book = this.createFromForm();
    if (book.id !== undefined) {
      this.subscribeToSaveResponse(this.bookService.update(book));
    } else {
      this.subscribeToSaveResponse(this.bookService.create(book));
    }
  }

  trackCategoryById(index: number, item: ICategory): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBook>>): void {
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

  protected updateForm(book: IBook): void {
    this.editForm.patchValue({
      id: book.id,
      name: book.name,
      imageUrl: book.imageUrl,
      pages: book.pages,
      status: book.status,
      likes: book.likes,
      createdBy: book.createdBy,
      createdDate: book.createdDate ? book.createdDate.format(DATE_TIME_FORMAT) : null,
      lastModifiedBy: book.lastModifiedBy,
      lastModifiedDate: book.lastModifiedDate ? book.lastModifiedDate.format(DATE_TIME_FORMAT) : null,
      category: book.category,
    });

    this.categoriesSharedCollection = this.categoryService.addCategoryToCollectionIfMissing(this.categoriesSharedCollection, book.category);
  }

  protected loadRelationshipsOptions(): void {
    this.categoryService
      .query()
      .pipe(map((res: HttpResponse<ICategory[]>) => res.body ?? []))
      .pipe(
        map((categories: ICategory[]) =>
          this.categoryService.addCategoryToCollectionIfMissing(categories, this.editForm.get('category')!.value)
        )
      )
      .subscribe((categories: ICategory[]) => (this.categoriesSharedCollection = categories));
  }

  protected createFromForm(): IBook {
    return {
      ...new Book(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      imageUrl: this.editForm.get(['imageUrl'])!.value,
      pages: this.editForm.get(['pages'])!.value,
      status: this.editForm.get(['status'])!.value,
      likes: this.editForm.get(['likes'])!.value,
      createdBy: this.editForm.get(['createdBy'])!.value,
      createdDate: this.editForm.get(['createdDate'])!.value
        ? dayjs(this.editForm.get(['createdDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      lastModifiedBy: this.editForm.get(['lastModifiedBy'])!.value,
      lastModifiedDate: this.editForm.get(['lastModifiedDate'])!.value
        ? dayjs(this.editForm.get(['lastModifiedDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      category: this.editForm.get(['category'])!.value,
    };
  }
}
