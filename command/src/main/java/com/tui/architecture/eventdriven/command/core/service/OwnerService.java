package com.tui.architecture.eventdriven.command.core.service;

import com.tui.architecture.eventdriven.command.core.event.DataModifiedEvent;
import com.tui.architecture.eventdriven.command.core.event.EEventOperation;
import com.tui.architecture.eventdriven.command.core.mapper.OwnerDtoEntityMapper;
import com.tui.architecture.eventdriven.command.db.entity.CarEntity;
import com.tui.architecture.eventdriven.command.db.entity.OwnerEntity;
import com.tui.architecture.eventdriven.command.db.repository.CarRepository;
import com.tui.architecture.eventdriven.command.db.repository.OwnerRepository;
import com.tui.architecture.eventdriven.command.dto.OwnerDTO;
import com.tui.architecture.eventdriven.command.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/*
 * Service for owner entity commands
 *
 * @author joseluis.nogueira on 28/08/2019
 */
@Service
@Slf4j
@Transactional
public class OwnerService {
  @Autowired
  private OwnerDtoEntityMapper ownerDtoEntityMapper;
  @Autowired
  private OwnerRepository ownerRepository;
  @Autowired
  private CarService carService;
  @Autowired
  private CarRepository carRepository;
  @Autowired
  private ApplicationEventPublisher eventPublisher;

  /**
   * Create Owner entity
   *
   * @param ownerDTO
   * @return
   * @throws ServiceException
   */
  @Transactional
  public OwnerDTO create(OwnerDTO ownerDTO) throws ServiceException {
    if (ownerRepository.existsById(ownerDTO.getId())) {
      throw new ServiceException(HttpStatus.CONFLICT);
    }
    OwnerEntity ownerEntity;
    try {
      ownerEntity = ownerDtoEntityMapper.toEntity(ownerDTO);
      ownerRepository.save(ownerEntity);
      eventPublisher.publishEvent(new DataModifiedEvent(getKey(ownerDTO), OwnerDTO.MEDIA_TYPE, ownerDTO, EEventOperation.CREATED));
    } catch (DataIntegrityViolationException e) {
      throw new ServiceException(HttpStatus.CONFLICT, e);
    }
    return ownerDtoEntityMapper.toDTO(ownerEntity);
  }

  /**
   * Update Owner entity
   *
   * @param ownerDTO
   * @return
   * @throws ServiceException
   */
  @Transactional
  public OwnerDTO update(OwnerDTO ownerDTO) throws ServiceException {
    if (!ownerRepository.existsById(ownerDTO.getId())) {
      throw new ServiceException(HttpStatus.NOT_FOUND);
    }

    OwnerEntity ownerEntity = ownerDtoEntityMapper.toEntity(ownerDTO);
    ownerRepository.save(ownerEntity);
    eventPublisher.publishEvent(new DataModifiedEvent(getKey(ownerDTO), OwnerDTO.MEDIA_TYPE, ownerDTO, EEventOperation.UPDATED));

    return ownerDtoEntityMapper.toDTO(ownerEntity);
  }

  /**
   * Delete Owner entity
   *
   * @param id
   * @throws ServiceException
   */
  @Transactional
  public void delete(String id) throws ServiceException {
    Optional<OwnerEntity> ownerEntity = ownerRepository.findById(id);
    if (!ownerEntity.isPresent()) {
      throw new ServiceException(HttpStatus.NOT_FOUND);
    }

    List<CarEntity> carEntities = carRepository.findByOwner(id);
    if (!carEntities.isEmpty()) {
      for (CarEntity carEntity : carEntities) {
        carService.delete(carEntity.getRegistration());
      }
    }
    ownerRepository.deleteById(id);
    eventPublisher.publishEvent(new DataModifiedEvent(getKey(id), OwnerDTO.MEDIA_TYPE, ownerDtoEntityMapper.toDTO(ownerEntity.get()), EEventOperation.DELETED));
  }

  private String getKey(OwnerDTO ownerDTO){
    return "owner-" + ownerDTO.getId();
  }

  private String getKey(String id){
    return "owner-" + id;
  }

}
