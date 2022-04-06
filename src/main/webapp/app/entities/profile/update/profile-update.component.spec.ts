import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProfileService } from '../service/profile.service';
import { IProfile, Profile } from '../profile.model';
import { IImage } from 'app/entities/image/image.model';
import { ImageService } from 'app/entities/image/service/image.service';

import { ProfileUpdateComponent } from './profile-update.component';

describe('Profile Management Update Component', () => {
  let comp: ProfileUpdateComponent;
  let fixture: ComponentFixture<ProfileUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let profileService: ProfileService;
  let imageService: ImageService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProfileUpdateComponent],
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
      .overrideTemplate(ProfileUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProfileUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    profileService = TestBed.inject(ProfileService);
    imageService = TestBed.inject(ImageService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call image query and add missing value', () => {
      const profile: IProfile = { id: 456 };
      const image: IImage = { id: 24315 };
      profile.image = image;

      const imageCollection: IImage[] = [{ id: 79594 }];
      jest.spyOn(imageService, 'query').mockReturnValue(of(new HttpResponse({ body: imageCollection })));
      const expectedCollection: IImage[] = [image, ...imageCollection];
      jest.spyOn(imageService, 'addImageToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ profile });
      comp.ngOnInit();

      expect(imageService.query).toHaveBeenCalled();
      expect(imageService.addImageToCollectionIfMissing).toHaveBeenCalledWith(imageCollection, image);
      expect(comp.imagesCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const profile: IProfile = { id: 456 };
      const image: IImage = { id: 99007 };
      profile.image = image;

      activatedRoute.data = of({ profile });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(profile));
      expect(comp.imagesCollection).toContain(image);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Profile>>();
      const profile = { id: 123 };
      jest.spyOn(profileService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: profile }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(profileService.update).toHaveBeenCalledWith(profile);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Profile>>();
      const profile = new Profile();
      jest.spyOn(profileService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: profile }));
      saveSubject.complete();

      // THEN
      expect(profileService.create).toHaveBeenCalledWith(profile);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Profile>>();
      const profile = { id: 123 };
      jest.spyOn(profileService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(profileService.update).toHaveBeenCalledWith(profile);
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
  });
});
