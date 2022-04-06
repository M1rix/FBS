import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AuthorService } from '../service/author.service';
import { IAuthor, Author } from '../author.model';
import { IImage } from 'app/entities/image/image.model';
import { ImageService } from 'app/entities/image/service/image.service';
import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';

import { AuthorUpdateComponent } from './author-update.component';

describe('Author Management Update Component', () => {
  let comp: AuthorUpdateComponent;
  let fixture: ComponentFixture<AuthorUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let authorService: AuthorService;
  let imageService: ImageService;
  let bookService: BookService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AuthorUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(AuthorUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AuthorUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    authorService = TestBed.inject(AuthorService);
    imageService = TestBed.inject(ImageService);
    bookService = TestBed.inject(BookService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call image query and add missing value', () => {
      const author: IAuthor = { id: 456 };
      const image: IImage = { id: 18250 };
      author.image = image;

      const imageCollection: IImage[] = [{ id: 81585 }];
      jest.spyOn(imageService, 'query').mockReturnValue(of(new HttpResponse({ body: imageCollection })));
      const expectedCollection: IImage[] = [image, ...imageCollection];
      jest.spyOn(imageService, 'addImageToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ author });
      comp.ngOnInit();

      expect(imageService.query).toHaveBeenCalled();
      expect(imageService.addImageToCollectionIfMissing).toHaveBeenCalledWith(imageCollection, image);
      expect(comp.imagesCollection).toEqual(expectedCollection);
    });

    it('Should call Book query and add missing value', () => {
      const author: IAuthor = { id: 456 };
      const book: IBook = { id: 34948 };
      author.book = book;

      const bookCollection: IBook[] = [{ id: 48784 }];
      jest.spyOn(bookService, 'query').mockReturnValue(of(new HttpResponse({ body: bookCollection })));
      const additionalBooks = [book];
      const expectedCollection: IBook[] = [...additionalBooks, ...bookCollection];
      jest.spyOn(bookService, 'addBookToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ author });
      comp.ngOnInit();

      expect(bookService.query).toHaveBeenCalled();
      expect(bookService.addBookToCollectionIfMissing).toHaveBeenCalledWith(bookCollection, ...additionalBooks);
      expect(comp.booksSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const author: IAuthor = { id: 456 };
      const image: IImage = { id: 19121 };
      author.image = image;
      const book: IBook = { id: 17994 };
      author.book = book;

      activatedRoute.data = of({ author });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(author));
      expect(comp.imagesCollection).toContain(image);
      expect(comp.booksSharedCollection).toContain(book);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Author>>();
      const author = { id: 123 };
      jest.spyOn(authorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ author });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: author }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(authorService.update).toHaveBeenCalledWith(author);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Author>>();
      const author = new Author();
      jest.spyOn(authorService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ author });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: author }));
      saveSubject.complete();

      // THEN
      expect(authorService.create).toHaveBeenCalledWith(author);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Author>>();
      const author = { id: 123 };
      jest.spyOn(authorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ author });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(authorService.update).toHaveBeenCalledWith(author);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackImageById', () => {
      it('Should return tracked Image primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackImageById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackBookById', () => {
      it('Should return tracked Book primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackBookById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
