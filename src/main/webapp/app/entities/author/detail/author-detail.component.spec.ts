import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AuthorDetailComponent } from './author-detail.component';

describe('Author Management Detail Component', () => {
  let comp: AuthorDetailComponent;
  let fixture: ComponentFixture<AuthorDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AuthorDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ author: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(AuthorDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(AuthorDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load author on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.author).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
