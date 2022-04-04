import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProfileDetailComponent } from './profile-detail.component';

describe('Profile Management Detail Component', () => {
  let comp: ProfileDetailComponent;
  let fixture: ComponentFixture<ProfileDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProfileDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ profile: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProfileDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProfileDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load profile on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.profile).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
