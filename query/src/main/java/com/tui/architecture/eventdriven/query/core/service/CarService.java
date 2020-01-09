package com.tui.architecture.eventdriven.query.core.service;

import com.googlecode.cqengine.resultset.ResultSet;
import com.tui.architecture.eventdriven.query.core.mapper.MemoryEntityMapper;
import com.tui.architecture.eventdriven.query.db.entity.MemoryEntity;
import com.tui.architecture.eventdriven.query.db.repository.MemoryRepository;
import com.tui.architecture.eventdriven.query.dto.event.CarDTO;
import com.tui.architecture.eventdriven.query.exception.EventException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 * Service for car entity query
 *
 * @author joseluis.nogueira on 10/09/2019
 */
@Service
public class CarService implements ICrudService<CarDTO> {
  @Autowired
  private MemoryRepository memoryRepository;
  @Autowired
  private MemoryEntityMapper memoryEntityMapper;

  @Override
  public void create(CarDTO dto) throws EventException {
    memoryRepository.save(dto);
  }

  @Override
  public void update(CarDTO dto) throws EventException {
    memoryRepository.save(dto);
  }

  @Override
  public void delete(CarDTO dto) throws EventException {
    ResultSet<MemoryEntity> resultSet = memoryRepository.findCarByRegistration(dto.getRegistration());
    if (resultSet.isNotEmpty()){
      memoryRepository.delete(memoryEntityMapper.toCarDTO(resultSet.uniqueResult()));
    }
  }
}
