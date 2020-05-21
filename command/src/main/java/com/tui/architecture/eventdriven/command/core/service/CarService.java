package com.tui.architecture.eventdriven.command.core.service;

import com.tui.architecture.eventdriven.command.core.event.DataModifiedEvent;
import com.tui.architecture.eventdriven.command.core.event.EEventOperation;
import com.tui.architecture.eventdriven.command.core.mapper.CarDtoEntityMapper;
import com.tui.architecture.eventdriven.command.db.entity.CarEntity;
import com.tui.architecture.eventdriven.command.db.repository.CarRepository;
import com.tui.architecture.eventdriven.command.dto.CarDTO;
import com.tui.architecture.eventdriven.command.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/*
 * Service for car entity commands
 *
 * @author joseluis.nogueira on 28/08/2019
 */
@Service
@Slf4j
@Transactional
public class CarService {
  @Autowired
  private CarDtoEntityMapper carDtoEntityMapper;
  @Autowired
  private CarRepository carRepository;
  @Autowired
  private ApplicationEventPublisher eventPublisher;

  /**
   * Create Car entity
   *
   * @param carDTO
   * @return
   * @throws ServiceException
   */
  public CarDTO create(CarDTO carDTO) throws ServiceException {
    if (carRepository.existsById(carDTO.getRegistration())) {
      throw new ServiceException(HttpStatus.CONFLICT);
    }
    CarEntity carEntity;
    try {
      carEntity = carDtoEntityMapper.toEntity(carDTO);
      carRepository.save(carEntity);
      eventPublisher.publishEvent(new DataModifiedEvent(getKey(carDTO), CarDTO.MEDIA_TYPE, carDTO, EEventOperation.CREATED));
    } catch (DataIntegrityViolationException e) {
      throw new ServiceException(HttpStatus.CONFLICT, e);
    }
    return carDtoEntityMapper.toDTO(carEntity);
  }

  /**
   * Update Car entity
   *
   * @param carDTO
   * @return
   * @throws ServiceException
   */
  public CarDTO update(CarDTO carDTO) throws ServiceException {
    if (!carRepository.existsById(carDTO.getRegistration())) {
      throw new ServiceException(HttpStatus.NOT_FOUND);
    }

    CarEntity carEntity = carDtoEntityMapper.toEntity(carDTO);
    carRepository.save(carEntity);
    eventPublisher.publishEvent(new DataModifiedEvent(getKey(carDTO), CarDTO.MEDIA_TYPE, carDTO, EEventOperation.UPDATED));

    return carDtoEntityMapper.toDTO(carEntity);
  }

  /**
   * Delete Car entity
   *
   * @param registration
   * @throws ServiceException
   */
  public void delete(String registration) throws ServiceException {
    Optional<CarEntity> carEntity = carRepository.findById(registration);
    if (!carEntity.isPresent()) {
      throw new ServiceException(HttpStatus.NOT_FOUND);
    }

    carRepository.deleteById(registration);
    eventPublisher.publishEvent(new DataModifiedEvent(getKey(registration), CarDTO.MEDIA_TYPE, carDtoEntityMapper.toDTO(carEntity.get()), EEventOperation.DELETED));
  }

  private String getKey(CarDTO carDTO){
    return "car-" + carDTO.getRegistration();
  }

  private String getKey(String registration){
    return "car-" + registration;
  }
}
