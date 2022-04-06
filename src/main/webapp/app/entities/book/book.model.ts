import dayjs from 'dayjs/esm';
import { IImage } from 'app/entities/image/image.model';
import { ICategory } from 'app/entities/category/category.model';
import { IAuthor } from 'app/entities/author/author.model';
import { BookStatus } from 'app/entities/enumerations/book-status.model';

export interface IBook {
  id?: number;
  name?: string | null;
  pages?: number | null;
  status?: BookStatus;
  likes?: number | null;
  createdBy?: string;
  createdDate?: dayjs.Dayjs;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  image?: IImage | null;
  category?: ICategory | null;
  authors?: IAuthor[] | null;
}

export class Book implements IBook {
  constructor(
    public id?: number,
    public name?: string | null,
    public pages?: number | null,
    public status?: BookStatus,
    public likes?: number | null,
    public createdBy?: string,
    public createdDate?: dayjs.Dayjs,
    public lastModifiedBy?: string | null,
    public lastModifiedDate?: dayjs.Dayjs | null,
    public image?: IImage | null,
    public category?: ICategory | null,
    public authors?: IAuthor[] | null
  ) {}
}

export function getBookIdentifier(book: IBook): number | undefined {
  return book.id;
}
