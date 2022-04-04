import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ExchangeDetailComponent } from './exchange-detail.component';

describe('Exchange Management Detail Component', () => {
  let comp: ExchangeDetailComponent;
  let fixture: ComponentFixture<ExchangeDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ExchangeDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ exchange: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ExchangeDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ExchangeDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load exchange on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.exchange).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
