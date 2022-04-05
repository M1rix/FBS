package uz.apextech.fbs.service.mapper;

import org.mapstruct.*;
import uz.apextech.fbs.domain.Exchange;
import uz.apextech.fbs.service.dto.ExchangeDTO;

/**
 * Mapper for the entity {@link Exchange} and its DTO {@link ExchangeDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProfileMapper.class, BookMapper.class })
public interface ExchangeMapper extends EntityMapper<ExchangeDTO, Exchange> {
    @Mapping(target = "formProfile", source = "formProfile", qualifiedByName = "id")
    @Mapping(target = "toProfile", source = "toProfile", qualifiedByName = "id")
    @Mapping(target = "book", source = "book", qualifiedByName = "name")
    ExchangeDTO toDto(Exchange s);
}
