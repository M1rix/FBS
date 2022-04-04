import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { BookDetailComponent } from './book-detail.component';

describe('Book Management Detail Component', () => {
  let comp: BookDetailComponent;
  let fixture: ComponentFixture<BookDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BookDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ book: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(BookDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(BookDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load book on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.book).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
