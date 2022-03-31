import dayjs from 'dayjs/esm';
import { IProfile } from 'app/entities/profile/profile.model';
import { IBook } from 'app/entities/book/book.model';

export interface IExchange {
  id?: number;
  createdBy?: string;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  formProfile?: IProfile | null;
  toProfile?: IProfile | null;
  book?: IBook | null;
}

export class Exchange implements IExchange {
  constructor(
    public id?: number,
    public createdBy?: string,
    public createdDate?: dayjs.Dayjs | null,
    public lastModifiedBy?: string | null,
    public lastModifiedDate?: dayjs.Dayjs | null,
    public formProfile?: IProfile | null,
    public toProfile?: IProfile | null,
    public book?: IBook | null
  ) {}
}

export function getExchangeIdentifier(exchange: IExchange): number | undefined {
  return exchange.id;
}
