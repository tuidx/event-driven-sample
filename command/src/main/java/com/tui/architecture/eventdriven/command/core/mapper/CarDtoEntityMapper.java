package com.tui.architecture.eventdriven.command.core.mapper;

import com.tui.architecture.eventdriven.command.db.entity.CarEntity;
import com.tui.architecture.eventdriven.command.dto.CarDTO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

/*
 * Mapper for car entity
 *
 * @author joseluis.nogueira on 28/08/2019
 */
@Mapper(
  componentModel = "spring",
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface CarDtoEntityMapper {
  CarDTO toDTO(CarEntity carEntity);

  CarEntity toEntity(CarDTO carDTO);
}
