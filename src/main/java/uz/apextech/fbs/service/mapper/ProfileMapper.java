package uz.apextech.fbs.service.mapper;

import org.mapstruct.*;
import uz.apextech.fbs.domain.Profile;
import uz.apextech.fbs.service.dto.ProfileDTO;

/**
 * Mapper for the entity {@link Profile} and its DTO {@link ProfileDTO}.
 */
@Mapper(componentModel = "spring", uses = { ImageMapper.class })
public interface ProfileMapper extends EntityMapper<ProfileDTO, Profile> {
    @Mapping(target = "image", source = "image", qualifiedByName = "url")
    ProfileDTO toDto(Profile s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoId(Profile profile);
}
