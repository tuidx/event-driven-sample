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

/*
 * Service for creation new data
 *
 * @author joseluis.nogueira on 11/10/2019
 */
@Service
@Slf4j
public class CreationService {
  @Autowired
  private CommandFeign commandFeign;
  @Autowired
  private QueryFeign queryFeign;
  @Autowired
  private ExecutorService connectionPool;

  private final Random random;

  public CreationService(){
    random = new Random();
  }

  public void createOwners(int elements) {
    List<Future<Void>> futures = new ArrayList<>();
    long next = recoverLastOnwerId();
    for (int i = 0; i < elements; i++) {
      OwnerDTO ownerDTO = new OwnerDTO();
      ownerDTO.setId("owner-" + (next + i));
      ownerDTO.setName(UUID.randomUUID().toString());
      ownerDTO.setSurname(UUID.randomUUID().toString());
      ownerDTO.setAge(random.nextInt(100) + 18);
      futures.add(connectionPool.submit(() -> {
        commandFeign.createOwner(ownerDTO);
        return null;
      }));
    }
    waitFutures(futures);
    log.info("Add new " + elements + " owners");
  }

  public void createCars(int elements) {
    ResponseEntity<List<OwnerDTO>> response = queryFeign.retrieveOwnerByCriteria();
    if (response.getStatusCode() != HttpStatus.OK || response.getBody().isEmpty()) {
      log.error("Error recovering owners");
      return;
    }

    long next = recoverLastCarRegistration();
    List<Future<Void>> futures = new ArrayList<>();
    List<OwnerDTO> ownerDTOS = response.getBody();
    for (int i = 0; i < elements; i++) {
      CarDTO carDTO = new CarDTO();
      carDTO.setRegistration("car-" + (next + i));
      carDTO.setOwner(ownerDTOS.get(random.nextInt(ownerDTOS.size())).getId());
      carDTO.setBrand(UUID.randomUUID().toString().substring(0, 30));
      carDTO.setModel(UUID.randomUUID().toString());
      carDTO.setColor(UUID.randomUUID().toString());
      carDTO.setYear(1950 + random.nextInt(100));
      futures.add(connectionPool.submit(() -> {
        commandFeign.createCar(carDTO);
        return null;
      }));
    }
    waitFutures(futures);
    log.info("Add new " + elements + " cars");
  }

  private long recoverLastOnwerId(){
    ResponseEntity<List<OwnerDTO>> responseEntity = queryFeign.retrieveOwnerByCriteria();
    if (responseEntity.getStatusCode() != HttpStatus.OK){
      log.error("Error recovering owners");
      return 0;
    }
    Long lastValue = (Long) responseEntity.getBody().stream().
      filter(e -> e.getId().startsWith("owner-")).
      map(e -> {
        try {
          return Long.parseLong(e.getId().substring(6));
        } catch (NumberFormatException ex) {
          return 0;
        }
      }).
      sorted((e1, e2) -> ((Comparable) e1).compareTo(e2) * -1).
      findFirst().orElse(null);

    return lastValue != null ? lastValue + 1 : 0;
  }

  private long recoverLastCarRegistration(){
    ResponseEntity<List<CarDTO>> responseEntity = queryFeign.retrieveCarByCriteria();
    if (responseEntity.getStatusCode() != HttpStatus.OK){
      log.error("Error recovering cars");
      return 0;
    }
    Long lastValue = (Long) responseEntity.getBody().stream().
      filter(e -> e.getRegistration().startsWith("car-")).
      map(e -> {
        try {
          return Long.parseLong(e.getRegistration().substring(4));
        } catch (NumberFormatException ex) {
          return 0;
        }
      }).
      sorted((e1, e2) -> ((Comparable) e1).compareTo(e2) * -1).
      findFirst().orElse(null);

    return lastValue != null ? lastValue + 1 : 0;
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
