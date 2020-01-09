package com.tui.architecture.eventdriven.stresstest.core.service;

import com.google.common.collect.Lists;
import com.tui.architecture.eventdriven.stresstest.core.feign.QueryFeign;
import com.tui.architecture.eventdriven.stresstest.core.mapper.CarDtoEntityMapper;
import com.tui.architecture.eventdriven.stresstest.core.mapper.OwnerDtoEntityMapper;
import com.tui.architecture.eventdriven.stresstest.db.repository.CarRepository;
import com.tui.architecture.eventdriven.stresstest.db.repository.OwnerRepository;
import com.tui.architecture.eventdriven.stresstest.dto.CarDTO;
import com.tui.architecture.eventdriven.stresstest.dto.OwnerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
 * Compare data between command and query
 *
 * @author joseluis.nogueira on 11/10/2019
 */
@Service
@Slf4j
public class ComparatorService {
  @Autowired
  private OwnerRepository ownerRepository;
  @Autowired
  private OwnerDtoEntityMapper ownerDtoEntityMapper;
  @Autowired
  private QueryFeign queryFeign;

  @Autowired
  private CarRepository carRepository;
  @Autowired
  private CarDtoEntityMapper carDtoEntityMapper;

  public List<String> run(){
    List<String> diferences = new ArrayList<>();
    diferences.addAll(compareOwners());
    diferences.addAll(compareCars());
    return diferences;
  }

  public List<String> compareOwners(){
    List<String> diferences = new ArrayList<>();
    Map<String, OwnerDTO> ownersCommand = ownerRepository.findAll().stream().map(e -> ownerDtoEntityMapper.toDTO(e)).collect(Collectors.toMap(OwnerDTO::getId, Function.identity()));

    ResponseEntity<List<OwnerDTO>> responseEntity = queryFeign.retrieveOwnerByCriteria();
    if (responseEntity.getStatusCode() != HttpStatus.OK){
      log.error("Error retrieving owners");
      return diferences;
    }

    Map<String, OwnerDTO> ownersQuery = responseEntity.getBody().stream().collect(Collectors.toMap(OwnerDTO::getId, Function.identity()));

    List<String> keys = Lists.newArrayList(ownersCommand.keySet());
    keys.removeAll(ownersQuery.keySet());
    if (!keys.isEmpty()){
      for(String key : keys){
        diferences.add("Owner with key " + key + " exists only in Command");
      }
    }

    keys = Lists.newArrayList(ownersQuery.keySet());
    keys.removeAll(ownersQuery.keySet());
    if (!keys.isEmpty()){
      for(String key : keys){
        diferences.add("Owner with key " + key + " exists only in Query");
      }
    }

    for(OwnerDTO ownerCommand : ownersCommand.values()){
      OwnerDTO ownerQuery = ownersQuery.get(ownerCommand.getId());
      if (ownerQuery != null && !ownerQuery.equals(ownerCommand)){
        diferences.add("Owner with differences between command = " + ownerCommand.toString() + " and query = " + ownerQuery.toString());
      }
    }

    if (diferences.isEmpty()){
      log.info("All owners are identical");
    }
    return diferences;
  }

  public List<String> compareCars(){
    List<String> diferences = new ArrayList<>();
    Map<String, CarDTO> carsCommand = carRepository.findAll().stream().map(e -> carDtoEntityMapper.toDTO(e)).collect(Collectors.toMap(CarDTO::getRegistration, Function.identity()));

    ResponseEntity<List<CarDTO>> responseEntity = queryFeign.retrieveCarByCriteria();
    if (responseEntity.getStatusCode() != HttpStatus.OK){
      log.error("Error retrieving cars");
      return diferences;
    }

    Map<String, CarDTO> carsQuery = responseEntity.getBody().stream().collect(Collectors.toMap(CarDTO::getRegistration, Function.identity()));

    List<String> keys = Lists.newArrayList(carsCommand.keySet());
    keys.removeAll(carsQuery.keySet());
    if (!keys.isEmpty()){
      for(String key : keys){
        diferences.add("Car with registration " + key + " exists only in Command");
      }
    }

    keys = Lists.newArrayList(carsQuery.keySet());
    keys.removeAll(carsQuery.keySet());
    if (!keys.isEmpty()){
      for(String key : keys){
        diferences.add("Car with registration " + key + " exists only in Query");
      }
    }

    for(CarDTO carCommand : carsCommand.values()){
      CarDTO carQuery = carsQuery.get(carsCommand);
      if (carQuery != null && !carQuery.equals(carCommand)){
        diferences.add("Car with differences between command = " + carCommand.toString() + " and query = " + carQuery.toString());
      }
    }
    if (diferences.isEmpty()){
      log.info("All cars are identical");
    }
    return diferences;
  }
}
