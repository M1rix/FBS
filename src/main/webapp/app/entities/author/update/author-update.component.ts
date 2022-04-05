import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IAuthor, Author } from '../author.model';
import { AuthorService } from '../service/author.service';
import { IImage } from 'app/entities/image/image.model';
import { ImageService } from 'app/entities/image/service/image.service';
import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';

@Component({
  selector: 'jhi-author-update',
  templateUrl: './author-update.component.html',
})
export class AuthorUpdateComponent implements OnInit {
  isSaving = false;

  imagesCollection: IImage[] = [];
  booksSharedCollection: IBook[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.maxLength(50)]],
    lastName: [null, [Validators.maxLength(50)]],
    createdBy: [null, [Validators.required, Validators.maxLength(50)]],
    createdDate: [],
    lastModifiedBy: [null, [Validators.maxLength(50)]],
    lastModifiedDate: [],
    image: [],
    book: [],
  });

  constructor(
    protected authorService: AuthorService,
    protected imageService: ImageService,
    protected bookService: BookService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ author }) => {
      if (author.id === undefined) {
        const today = dayjs().startOf('day');
        author.createdDate = today;
        author.lastModifiedDate = today;
      }

      this.updateForm(author);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const author = this.createFromForm();
    if (author.id !== undefined) {
      this.subscribeToSaveResponse(this.authorService.update(author));
    } else {
      this.subscribeToSaveResponse(this.authorService.create(author));
    }
  }

  trackImageById(index: number, item: IImage): number {
    return item.id!;
  }

  trackBookById(index: number, item: IBook): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAuthor>>): void {
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

  protected updateForm(author: IAuthor): void {
    this.editForm.patchValue({
      id: author.id,
      name: author.name,
      lastName: author.lastName,
      createdBy: author.createdBy,
      createdDate: author.createdDate ? author.createdDate.format(DATE_TIME_FORMAT) : null,
      lastModifiedBy: author.lastModifiedBy,
      lastModifiedDate: author.lastModifiedDate ? author.lastModifiedDate.format(DATE_TIME_FORMAT) : null,
      image: author.image,
      book: author.book,
    });

    this.imagesCollection = this.imageService.addImageToCollectionIfMissing(this.imagesCollection, author.image);
    this.booksSharedCollection = this.bookService.addBookToCollectionIfMissing(this.booksSharedCollection, author.book);
  }

  protected loadRelationshipsOptions(): void {
    this.imageService
      .query({ 'authorId.specified': 'false' })
      .pipe(map((res: HttpResponse<IImage[]>) => res.body ?? []))
      .pipe(map((images: IImage[]) => this.imageService.addImageToCollectionIfMissing(images, this.editForm.get('image')!.value)))
      .subscribe((images: IImage[]) => (this.imagesCollection = images));

    this.bookService
      .query()
      .pipe(map((res: HttpResponse<IBook[]>) => res.body ?? []))
      .pipe(map((books: IBook[]) => this.bookService.addBookToCollectionIfMissing(books, this.editForm.get('book')!.value)))
      .subscribe((books: IBook[]) => (this.booksSharedCollection = books));
  }

  protected createFromForm(): IAuthor {
    return {
      ...new Author(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      lastName: this.editForm.get(['lastName'])!.value,
      createdBy: this.editForm.get(['createdBy'])!.value,
      createdDate: this.editForm.get(['createdDate'])!.value
        ? dayjs(this.editForm.get(['createdDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      lastModifiedBy: this.editForm.get(['lastModifiedBy'])!.value,
      lastModifiedDate: this.editForm.get(['lastModifiedDate'])!.value
        ? dayjs(this.editForm.get(['lastModifiedDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      image: this.editForm.get(['image'])!.value,
      book: this.editForm.get(['book'])!.value,
    };
  }
}
