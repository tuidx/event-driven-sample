package com.tui.architecture.eventdriven.query.core.mapper;

import com.tui.architecture.eventdriven.query.db.entity.MemoryEntity;
import com.tui.architecture.eventdriven.query.dto.response.ResponseCarDTO;
import com.tui.architecture.eventdriven.query.dto.response.ResponseOwnerDTO;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/*
 * Test of MemoryEntityMapper
 *
 * @author joseluis.nogueira on 12/09/2019
 */
public class MemoryEntityMapperTest {
  private MemoryEntityMapper memoryEntityMapper;

  @Before
  public void setUp(){
    memoryEntityMapper = new MemoryEntityMapperImpl();
  }

  @Test
  public void toResponseOwnerDTO(){
    List<MemoryEntity> memoryEntities = new ArrayList<>();
    memoryEntities.add(createMemoryEntity("Owner Id 1", "Car registration 1"));
    memoryEntities.add(createMemoryEntity("Owner Id 2", "Car registration 2"));
    memoryEntities.add(createMemoryEntity("Owner Id 1", "Car registration 3"));
    memoryEntities.add(createMemoryEntity("Owner Id 3", null));

    List<ResponseOwnerDTO> responseOwnerDTOS = memoryEntityMapper.toResponseOwnerDTO(memoryEntities);

    assertTrue(responseOwnerDTOS.size() == 3);
    assertTrue(responseOwnerDTOS.stream().filter(e -> "Owner Id 1".equals(e.getId())).findFirst().get().getCarDTOS().size() == 2);
    assertTrue(responseOwnerDTOS.stream().filter(e -> "Owner Id 2".equals(e.getId())).findFirst().get().getCarDTOS().size() == 1);
    assertTrue(responseOwnerDTOS.stream().filter(e -> "Owner Id 3".equals(e.getId())).findFirst().get().getCarDTOS().isEmpty());
  }

  @Test
  public void toResponseCarDTO(){
    List<MemoryEntity> memoryEntities = new ArrayList<>();
    memoryEntities.add(createMemoryEntity("Owner Id 1", "Car registration 1"));
    memoryEntities.add(createMemoryEntity("Owner Id 2", "Car registration 2"));
    memoryEntities.add(createMemoryEntity("Owner Id 1", "Car registration 3"));
    memoryEntities.add(createMemoryEntity("Owner Id 3", null));

    List<ResponseCarDTO> responseCarDTOS = memoryEntityMapper.toResponseCarDTO(memoryEntities);

    assertTrue(responseCarDTOS.size() == 3 && responseCarDTOS.stream().filter(e -> e.getRegistration() != null).collect(Collectors.groupingBy(ResponseCarDTO::getRegistration)).size() == 3);
    assertTrue(responseCarDTOS.stream().filter(e -> "Owner Id 1".equals(e.getOwnerDTO().getId())).count() == 2);
    assertTrue(responseCarDTOS.stream().filter(e -> "Owner Id 2".equals(e.getOwnerDTO().getId())).count() == 1);
    assertTrue(responseCarDTOS.stream().filter(e -> "Owner Id 3".equals(e.getOwnerDTO().getId())).count() == 0);
  }

  private MemoryEntity createMemoryEntity(String id, String registration){
    MemoryEntity memoryEntity = new MemoryEntity();
    memoryEntity.setId(id);
    memoryEntity.setRegistration(registration);
    return memoryEntity;
  }
}