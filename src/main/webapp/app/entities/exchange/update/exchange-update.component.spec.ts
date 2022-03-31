import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ExchangeService } from '../service/exchange.service';
import { IExchange, Exchange } from '../exchange.model';
import { IProfile } from 'app/entities/profile/profile.model';
import { ProfileService } from 'app/entities/profile/service/profile.service';
import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';

import { ExchangeUpdateComponent } from './exchange-update.component';

describe('Exchange Management Update Component', () => {
  let comp: ExchangeUpdateComponent;
  let fixture: ComponentFixture<ExchangeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let exchangeService: ExchangeService;
  let profileService: ProfileService;
  let bookService: BookService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ExchangeUpdateComponent],
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
      .overrideTemplate(ExchangeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ExchangeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    exchangeService = TestBed.inject(ExchangeService);
    profileService = TestBed.inject(ProfileService);
    bookService = TestBed.inject(BookService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call formProfile query and add missing value', () => {
      const exchange: IExchange = { id: 456 };
      const formProfile: IProfile = { id: 10840 };
      exchange.formProfile = formProfile;

      const formProfileCollection: IProfile[] = [{ id: 44758 }];
      jest.spyOn(profileService, 'query').mockReturnValue(of(new HttpResponse({ body: formProfileCollection })));
      const expectedCollection: IProfile[] = [formProfile, ...formProfileCollection];
      jest.spyOn(profileService, 'addProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ exchange });
      comp.ngOnInit();

      expect(profileService.query).toHaveBeenCalled();
      expect(profileService.addProfileToCollectionIfMissing).toHaveBeenCalledWith(formProfileCollection, formProfile);
      expect(comp.formProfilesCollection).toEqual(expectedCollection);
    });

    it('Should call toProfile query and add missing value', () => {
      const exchange: IExchange = { id: 456 };
      const toProfile: IProfile = { id: 30295 };
      exchange.toProfile = toProfile;

      const toProfileCollection: IProfile[] = [{ id: 85201 }];
      jest.spyOn(profileService, 'query').mockReturnValue(of(new HttpResponse({ body: toProfileCollection })));
      const expectedCollection: IProfile[] = [toProfile, ...toProfileCollection];
      jest.spyOn(profileService, 'addProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ exchange });
      comp.ngOnInit();

      expect(profileService.query).toHaveBeenCalled();
      expect(profileService.addProfileToCollectionIfMissing).toHaveBeenCalledWith(toProfileCollection, toProfile);
      expect(comp.toProfilesCollection).toEqual(expectedCollection);
    });

    it('Should call book query and add missing value', () => {
      const exchange: IExchange = { id: 456 };
      const book: IBook = { id: 95178 };
      exchange.book = book;

      const bookCollection: IBook[] = [{ id: 89917 }];
      jest.spyOn(bookService, 'query').mockReturnValue(of(new HttpResponse({ body: bookCollection })));
      const expectedCollection: IBook[] = [book, ...bookCollection];
      jest.spyOn(bookService, 'addBookToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ exchange });
      comp.ngOnInit();

      expect(bookService.query).toHaveBeenCalled();
      expect(bookService.addBookToCollectionIfMissing).toHaveBeenCalledWith(bookCollection, book);
      expect(comp.booksCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const exchange: IExchange = { id: 456 };
      const formProfile: IProfile = { id: 4311 };
      exchange.formProfile = formProfile;
      const toProfile: IProfile = { id: 52727 };
      exchange.toProfile = toProfile;
      const book: IBook = { id: 24555 };
      exchange.book = book;

      activatedRoute.data = of({ exchange });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(exchange));
      expect(comp.formProfilesCollection).toContain(formProfile);
      expect(comp.toProfilesCollection).toContain(toProfile);
      expect(comp.booksCollection).toContain(book);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Exchange>>();
      const exchange = { id: 123 };
      jest.spyOn(exchangeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ exchange });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: exchange }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(exchangeService.update).toHaveBeenCalledWith(exchange);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Exchange>>();
      const exchange = new Exchange();
      jest.spyOn(exchangeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ exchange });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: exchange }));
      saveSubject.complete();

      // THEN
      expect(exchangeService.create).toHaveBeenCalledWith(exchange);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Exchange>>();
      const exchange = { id: 123 };
      jest.spyOn(exchangeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ exchange });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(exchangeService.update).toHaveBeenCalledWith(exchange);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackProfileById', () => {
      it('Should return tracked Profile primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackProfileById(0, entity);
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
