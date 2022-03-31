import dayjs from 'dayjs/esm';
import { IBook } from 'app/entities/book/book.model';

export interface ICategory {
  id?: number;
  name?: string;
  createdBy?: string;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  books?: IBook[] | null;
}

export class Category implements ICategory {
  constructor(
    public id?: number,
    public name?: string,
    public createdBy?: string,
    public createdDate?: dayjs.Dayjs | null,
    public lastModifiedBy?: string | null,
    public lastModifiedDate?: dayjs.Dayjs | null,
    public books?: IBook[] | null
  ) {}
}

export function getCategoryIdentifier(category: ICategory): number | undefined {
  return category.id;
}
