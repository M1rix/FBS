import dayjs from 'dayjs/esm';
import { ICategory } from 'app/entities/category/category.model';
import { IExchange } from 'app/entities/exchange/exchange.model';
import { IAuthor } from 'app/entities/author/author.model';
import { BookStatus } from 'app/entities/enumerations/book-status.model';

export interface IBook {
  id?: number;
  name?: string | null;
  imageUrl?: string;
  pages?: number | null;
  status?: BookStatus;
  likes?: number | null;
  createdBy?: string;
  createdDate?: dayjs.Dayjs;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  category?: ICategory | null;
  exchange?: IExchange | null;
  authors?: IAuthor[] | null;
}

export class Book implements IBook {
  constructor(
    public id?: number,
    public name?: string | null,
    public imageUrl?: string,
    public pages?: number | null,
    public status?: BookStatus,
    public likes?: number | null,
    public createdBy?: string,
    public createdDate?: dayjs.Dayjs,
    public lastModifiedBy?: string | null,
    public lastModifiedDate?: dayjs.Dayjs | null,
    public category?: ICategory | null,
    public exchange?: IExchange | null,
    public authors?: IAuthor[] | null
  ) {}
}

export function getBookIdentifier(book: IBook): number | undefined {
  return book.id;
}
