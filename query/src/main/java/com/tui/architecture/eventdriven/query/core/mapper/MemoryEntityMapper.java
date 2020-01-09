package com.tui.architecture.eventdriven.query.core.mapper;

import com.tui.architecture.eventdriven.query.db.entity.MemoryEntity;
import com.tui.architecture.eventdriven.query.dto.event.CarDTO;
import com.tui.architecture.eventdriven.query.dto.event.OwnerDTO;
import com.tui.architecture.eventdriven.query.dto.response.ResponseCarDTO;
import com.tui.architecture.eventdriven.query.dto.response.ResponseOwnerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

import java.util.List;
import java.util.stream.Collectors;

/*
 * Mapper MemoryEntity <-> OwnerDTO / CarDTO
 *
 * @author joseluis.nogueira on 12/09/2019
 */
@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface MemoryEntityMapper {
  MemoryEntity toMemoryEntity(OwnerDTO ownerDTO);
  MemoryEntity toMemoryEntity(OwnerDTO ownerDTO, CarDTO carDTO);

  OwnerDTO toOwnerDTO(MemoryEntity memoryEntity);
  @Mapping(source = "id", target = "owner")
  CarDTO toCarDTO(MemoryEntity memoryEntity);

  ResponseOwnerDTO toResponseOwnerDTO(MemoryEntity memoryEntity);
  ResponseCarDTO toResponseCarDTO(MemoryEntity memoryEntity);

  default List<ResponseOwnerDTO> toResponseOwnerDTO(List<MemoryEntity> memoryEntities){
    return memoryEntities.stream().collect(Collectors.groupingBy(MemoryEntity::getId)).entrySet().stream().map(e -> {
      List<CarDTO> carDTOS = e.getValue().stream().filter(f -> f.getRegistration() != null).map(this::toCarDTO).collect(Collectors.toList());

      ResponseOwnerDTO responseOwnerDTO = toResponseOwnerDTO(e.getValue().get(0));
      responseOwnerDTO.setCarDTOS(carDTOS);
      return responseOwnerDTO;
    }).collect(Collectors.toList());
  }

  default List<ResponseCarDTO> toResponseCarDTO(List<MemoryEntity> memoryEntities){
    return memoryEntities.stream().filter(e -> e.getRegistration() != null).map(e -> {
      ResponseCarDTO responseCarDTO = toResponseCarDTO(e);
      responseCarDTO.setOwnerDTO(toOwnerDTO(e));
      return responseCarDTO;
    }).collect(Collectors.toList());
  }


}
