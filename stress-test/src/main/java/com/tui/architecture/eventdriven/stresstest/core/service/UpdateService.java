package com.tui.architecture.eventdriven.stresstest.core.service;

import com.tui.architecture.eventdriven.stresstest.core.feign.CommandFeign;
import com.tui.architecture.eventdriven.stresstest.core.feign.QueryFeign;
import com.tui.architecture.eventdriven.stresstest.dto.CarDTO;
import com.tui.architecture.eventdriven.stresstest.dto.OwnerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/*
 * Service for modify data
 *
 * @author joseluis.nogueira on 14/10/2019
 */
@Service
@Slf4j
public class UpdateService {
  @Autowired
  private CommandFeign commandFeign;
  @Autowired
  private QueryFeign queryFeign;
  @Autowired
  private ExecutorService connectionPool;

  private final Random random;

  public UpdateService(){
    random = new Random();
  }

  public void updateOwners(int elements, boolean parallel) {
    List<String> ownersIds = getOnwersIds();
    if (ownersIds.isEmpty()) {
      return;
    }

    List<Future<Void>> futures = new ArrayList<>();
    for (int i = 0; i < elements; i++) {
      String id = ownersIds.get(random.nextInt(ownersIds.size()));

      if (parallel){
        futures.add(connectionPool.submit(() -> {
          updateOneOwner(id);
          return null;
        }));
      } else {
        updateOneOwner(id);
      }
    }
    waitFutures(futures);
    log.info("Modified " + elements + " owners");
  }

  public void updateCars(int elements, boolean parallel) {
    List<String> ownersIds = getOnwersIds();
    if (ownersIds.isEmpty()) {
      return;
    }
    List<String> carsRegistrations = getCarsRegistrations();
    if (carsRegistrations.isEmpty()) {
      return;
    }

    List<Future<Void>> futures = new ArrayList<>();
    for (int i = 0; i < elements; i++) {
      String registration = carsRegistrations.get(random.nextInt(carsRegistrations.size()));

      if (parallel){
        futures.add(connectionPool.submit(() -> {
          updateOneCar(registration, ownersIds);
          return null;
        }));
      } else {
        updateOneCar(registration, ownersIds);
      }
    }
    waitFutures(futures);
    log.info("Modified " + elements + " cars");
  }

  private List<String> getOnwersIds() {
    ResponseEntity<List<OwnerDTO>> responseEntity = queryFeign.retrieveOwnerByCriteria();
    if (responseEntity.getStatusCode() != HttpStatus.OK) {
      log.error("Error recovering owners");
      return new ArrayList<>();
    }
    return responseEntity.getBody().stream().filter(e -> e.getId().startsWith("owner-")).map(OwnerDTO::getId).collect(Collectors.toList());
  }

  private List<String> getCarsRegistrations() {
    ResponseEntity<List<CarDTO>> responseEntity = queryFeign.retrieveCarByCriteria();
    if (responseEntity.getStatusCode() != HttpStatus.OK) {
      log.error("Error recovering cars");
      return new ArrayList<>();
    }
    return responseEntity.getBody().stream().filter(e -> e.getRegistration().startsWith("car-")).map(CarDTO::getRegistration).collect(Collectors.toList());
  }

  private void updateOneOwner(String id){
    ResponseEntity response = queryFeign.retrieveOwner(id);
    OwnerDTO ownerDTO = (OwnerDTO) response.getBody();
    int changeValue = random.nextInt(3);
    if (changeValue == 0) {
      ownerDTO.setName(UUID.randomUUID().toString());
    } else if (changeValue == 1) {
      ownerDTO.setSurname(UUID.randomUUID().toString());
    } else if (changeValue == 2) {
      ownerDTO.setAge(random.nextInt(100) + 18);
    }

    commandFeign.updateOwner(ownerDTO);
  }

  private void updateOneCar(String registration, List<String> ownersIds){
    ResponseEntity response = queryFeign.retrieveCar(registration);
    CarDTO carDTO = (CarDTO) response.getBody();
    int changeValue = random.nextInt(5);
    if (changeValue == 0) {
      carDTO.setOwner(ownersIds.get(random.nextInt(ownersIds.size())));
    } else if (changeValue == 1) {
      carDTO.setBrand(UUID.randomUUID().toString().substring(0, 30));
    } else if (changeValue == 2) {
      carDTO.setModel(UUID.randomUUID().toString());
    } else if (changeValue == 3) {
      carDTO.setColor(UUID.randomUUID().toString());
    } else if (changeValue == 4) {
      carDTO.setYear(1950 + random.nextInt(100));
    }
    commandFeign.updateCar(carDTO);
  }


  private void waitFutures(List<Future<Void>> futures){
    futures.forEach(e -> {
      try {
        e.get();
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
      } catch (ExecutionException ex) {
        log.error("Error executing operation", e);
      }
    });
  }
}
