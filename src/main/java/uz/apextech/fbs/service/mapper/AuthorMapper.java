package uz.apextech.fbs.service.mapper;

import org.mapstruct.*;
import uz.apextech.fbs.domain.Author;
import uz.apextech.fbs.service.dto.AuthorDTO;

/**
 * Mapper for the entity {@link Author} and its DTO {@link AuthorDTO}.
 */
@Mapper(componentModel = "spring", uses = { BookMapper.class })
public interface AuthorMapper extends EntityMapper<AuthorDTO, Author> {
    @Mapping(target = "book", source = "book", qualifiedByName = "id")
    AuthorDTO toDto(Author s);
}
