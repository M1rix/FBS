import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IProfile, Profile } from '../profile.model';
import { ProfileService } from '../service/profile.service';
import { Gender } from 'app/entities/enumerations/gender.model';
import { ProfileStatus } from 'app/entities/enumerations/profile-status.model';

@Component({
  selector: 'jhi-profile-update',
  templateUrl: './profile-update.component.html',
})
export class ProfileUpdateComponent implements OnInit {
  isSaving = false;
  genderValues = Object.keys(Gender);
  profileStatusValues = Object.keys(ProfileStatus);

  editForm = this.fb.group({
    id: [],
    phone: [null, [Validators.required, Validators.minLength(12), Validators.maxLength(12)]],
    accessToken: [null, [Validators.required, Validators.minLength(32), Validators.maxLength(255)]],
    firstName: [null, [Validators.maxLength(50)]],
    lastName: [null, [Validators.maxLength(50)]],
    imageUrl: [null, [Validators.required, Validators.maxLength(255)]],
    langKey: [null, [Validators.required, Validators.minLength(2), Validators.maxLength(6)]],
    gender: [],
    score: [null, [Validators.min(0), Validators.max(5)]],
    likes: [null, [Validators.min(0)]],
    status: [null, [Validators.required]],
    createdBy: [null, [Validators.required, Validators.maxLength(50)]],
    createdDate: [],
    lastModifiedBy: [null, [Validators.maxLength(50)]],
    lastModifiedDate: [],
  });

  constructor(protected profileService: ProfileService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ profile }) => {
      if (profile.id === undefined) {
        const today = dayjs().startOf('day');
        profile.createdDate = today;
        profile.lastModifiedDate = today;
      }

      this.updateForm(profile);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const profile = this.createFromForm();
    if (profile.id !== undefined) {
      this.subscribeToSaveResponse(this.profileService.update(profile));
    } else {
      this.subscribeToSaveResponse(this.profileService.create(profile));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProfile>>): void {
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

  protected updateForm(profile: IProfile): void {
    this.editForm.patchValue({
      id: profile.id,
      phone: profile.phone,
      accessToken: profile.accessToken,
      firstName: profile.firstName,
      lastName: profile.lastName,
      imageUrl: profile.imageUrl,
      langKey: profile.langKey,
      gender: profile.gender,
      score: profile.score,
      likes: profile.likes,
      status: profile.status,
      createdBy: profile.createdBy,
      createdDate: profile.createdDate ? profile.createdDate.format(DATE_TIME_FORMAT) : null,
      lastModifiedBy: profile.lastModifiedBy,
      lastModifiedDate: profile.lastModifiedDate ? profile.lastModifiedDate.format(DATE_TIME_FORMAT) : null,
    });
  }

  protected createFromForm(): IProfile {
    return {
      ...new Profile(),
      id: this.editForm.get(['id'])!.value,
      phone: this.editForm.get(['phone'])!.value,
      accessToken: this.editForm.get(['accessToken'])!.value,
      firstName: this.editForm.get(['firstName'])!.value,
      lastName: this.editForm.get(['lastName'])!.value,
      imageUrl: this.editForm.get(['imageUrl'])!.value,
      langKey: this.editForm.get(['langKey'])!.value,
      gender: this.editForm.get(['gender'])!.value,
      score: this.editForm.get(['score'])!.value,
      likes: this.editForm.get(['likes'])!.value,
      status: this.editForm.get(['status'])!.value,
      createdBy: this.editForm.get(['createdBy'])!.value,
      createdDate: this.editForm.get(['createdDate'])!.value
        ? dayjs(this.editForm.get(['createdDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      lastModifiedBy: this.editForm.get(['lastModifiedBy'])!.value,
      lastModifiedDate: this.editForm.get(['lastModifiedDate'])!.value
        ? dayjs(this.editForm.get(['lastModifiedDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
    };
  }
}
