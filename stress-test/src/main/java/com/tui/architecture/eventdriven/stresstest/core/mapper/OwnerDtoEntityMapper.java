package com.tui.architecture.eventdriven.stresstest.core.mapper;

import com.tui.architecture.eventdriven.stresstest.db.entity.OwnerEntity;
import com.tui.architecture.eventdriven.stresstest.dto.OwnerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

/*
 * Mapper for owner entity
 *
 * @author joseluis.nogueira on 28/08/2019
 */
@Mapper(
  componentModel = "spring",
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface OwnerDtoEntityMapper {
  OwnerDTO toDTO(OwnerEntity ownerEntity);

  @Mapping(source = "id", target = "id")
  OwnerEntity toEntity(OwnerDTO ownerDTO);
}
