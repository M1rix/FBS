import dayjs from 'dayjs/esm';
import { IBook } from 'app/entities/book/book.model';

export interface IAuthor {
  id?: number;
  name?: string | null;
  lastName?: string | null;
  imageUrl?: string | null;
  createdBy?: string;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  book?: IBook | null;
}

export class Author implements IAuthor {
  constructor(
    public id?: number,
    public name?: string | null,
    public lastName?: string | null,
    public imageUrl?: string | null,
    public createdBy?: string,
    public createdDate?: dayjs.Dayjs | null,
    public lastModifiedBy?: string | null,
    public lastModifiedDate?: dayjs.Dayjs | null,
    public book?: IBook | null
  ) {}
}

export function getAuthorIdentifier(author: IAuthor): number | undefined {
  return author.id;
}
