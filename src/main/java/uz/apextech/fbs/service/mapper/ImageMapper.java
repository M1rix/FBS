package uz.apextech.fbs.service.mapper;

import org.mapstruct.*;
import uz.apextech.fbs.domain.Image;
import uz.apextech.fbs.service.dto.ImageDTO;

/**
 * Mapper for the entity {@link Image} and its DTO {@link ImageDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ImageMapper extends EntityMapper<ImageDTO, Image> {
    @Named("url")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "url", source = "url")
    ImageDTO toDtoUrl(Image image);
}
