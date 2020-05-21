package com.tui.architecture.eventdriven.query.core.service;

import com.googlecode.cqengine.resultset.ResultSet;
import com.tui.architecture.eventdriven.query.core.mapper.MemoryEntityMapper;
import com.tui.architecture.eventdriven.query.db.entity.MemoryEntity;
import com.tui.architecture.eventdriven.query.db.repository.MemoryRepository;
import com.tui.architecture.eventdriven.query.dto.event.OwnerDTO;
import com.tui.architecture.eventdriven.query.exception.EventException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 * Service for owner entity query
 *
 * @author joseluis.nogueira on 10/09/2019
 */
@Service
public class OwnerService implements ICrudService<OwnerDTO> {
  @Autowired
  private MemoryRepository memoryRepository;
  @Autowired
  private MemoryEntityMapper memoryEntityMapper;

  @Override
  public void create(OwnerDTO dto) throws EventException {
    memoryRepository.save(dto);
  }

  @Override
  public void update(OwnerDTO dto) throws EventException {
    memoryRepository.save(dto);
  }

  @Override
  public void delete(OwnerDTO ownerDTO) throws EventException {
    ResultSet<MemoryEntity> memoryEntities = memoryRepository.findOwnerById(ownerDTO.getId());
    if (memoryEntities.isNotEmpty()){
      memoryRepository.delete(memoryEntityMapper.toOwnerDTO(memoryEntities.uniqueResult()));
    }
  }
}
