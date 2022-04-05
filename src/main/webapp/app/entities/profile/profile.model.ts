import dayjs from 'dayjs/esm';
import { IImage } from 'app/entities/image/image.model';
import { Gender } from 'app/entities/enumerations/gender.model';
import { ProfileStatus } from 'app/entities/enumerations/profile-status.model';

export interface IProfile {
  id?: number;
  phone?: string;
  accessToken?: string;
  firstName?: string | null;
  lastName?: string | null;
  langKey?: string;
  gender?: Gender | null;
  score?: number | null;
  likes?: number | null;
  status?: ProfileStatus;
  createdBy?: string;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  image?: IImage | null;
}

export class Profile implements IProfile {
  constructor(
    public id?: number,
    public phone?: string,
    public accessToken?: string,
    public firstName?: string | null,
    public lastName?: string | null,
    public langKey?: string,
    public gender?: Gender | null,
    public score?: number | null,
    public likes?: number | null,
    public status?: ProfileStatus,
    public createdBy?: string,
    public createdDate?: dayjs.Dayjs | null,
    public lastModifiedBy?: string | null,
    public lastModifiedDate?: dayjs.Dayjs | null,
    public image?: IImage | null
  ) {}
}

export function getProfileIdentifier(profile: IProfile): number | undefined {
  return profile.id;
}
