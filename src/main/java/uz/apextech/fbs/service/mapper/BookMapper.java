package uz.apextech.fbs.service.mapper;

import org.mapstruct.*;
import uz.apextech.fbs.domain.Book;
import uz.apextech.fbs.service.dto.BookDTO;

/**
 * Mapper for the entity {@link Book} and its DTO {@link BookDTO}.
 */
@Mapper(componentModel = "spring", uses = { ImageMapper.class, CategoryMapper.class })
public interface BookMapper extends EntityMapper<BookDTO, Book> {
    @Mapping(target = "image", source = "image", qualifiedByName = "url")
    @Mapping(target = "category", source = "category", qualifiedByName = "id")
    BookDTO toDto(Book s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BookDTO toDtoId(Book book);

    @Named("name")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    BookDTO toDtoName(Book book);
}
