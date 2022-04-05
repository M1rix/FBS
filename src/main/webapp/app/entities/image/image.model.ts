import dayjs from 'dayjs/esm';

export interface IImage {
  id?: number;
  name?: string | null;
  dataContentType?: string;
  data?: string;
  url?: string | null;
  createdBy?: string;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}

export class Image implements IImage {
  constructor(
    public id?: number,
    public name?: string | null,
    public dataContentType?: string,
    public data?: string,
    public url?: string | null,
    public createdBy?: string,
    public createdDate?: dayjs.Dayjs | null,
    public lastModifiedBy?: string | null,
    public lastModifiedDate?: dayjs.Dayjs | null
  ) {}
}

export function getImageIdentifier(image: IImage): number | undefined {
  return image.id;
}
