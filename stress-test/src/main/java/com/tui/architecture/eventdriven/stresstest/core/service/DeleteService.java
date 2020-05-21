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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/*
 *
 *
 * @author joseluis.nogueira on 15/10/2019
 */
@Service
@Slf4j
public class DeleteService {
  @Autowired
  private CommandFeign commandFeign;
  @Autowired
  private QueryFeign queryFeign;
  @Autowired
  private ExecutorService connectionPool;

  private final Random random;

  public DeleteService(){
    random = new Random();
  }

  public int deleteOwners(int elements) {
    List<String> ownersIds = getOnwersIds();
    if (ownersIds.isEmpty()) {
      return 0;
    }

    int deleted = 0;
    List<Future<Void>> futures = new ArrayList<>();
    for (int i = 0; i < elements && !ownersIds.isEmpty(); i++) {
      String id = ownersIds.get(random.nextInt(ownersIds.size()));
      ownersIds.remove(id);
      futures.add(connectionPool.submit(() -> {
        commandFeign.deleteOwner(id);
        return null;
      }));
      deleted++;
    }
    waitFutures(futures);

    log.info("Deleted " + deleted + " owners");
    return deleted;
  }

  public int deleteCars(int elements) {
    List<String> carsRegistrations = getCarsRegistrations();
    if (carsRegistrations.isEmpty()) {
      return 0;
    }

    int deleted = 0;
    List<Future<Void>> futures = new ArrayList<>();
    for (int i = 0; i < elements && !carsRegistrations.isEmpty(); i++) {
      String registration = carsRegistrations.get(random.nextInt(carsRegistrations.size()));
      carsRegistrations.remove(registration);
      futures.add(connectionPool.submit(() -> {
        commandFeign.deleteCar(registration);
        return null;
      }));
      deleted++;
    }
    waitFutures(futures);
    log.info("Deleted " + deleted + " cars");
    return deleted;
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
