package com.tui.architecture.eventdriven.stresstest.controller;

import com.tui.architecture.eventdriven.stresstest.core.service.CleanService;
import com.tui.architecture.eventdriven.stresstest.core.service.ComparatorService;
import com.tui.architecture.eventdriven.stresstest.core.service.CreationService;
import com.tui.architecture.eventdriven.stresstest.core.service.DeleteService;
import com.tui.architecture.eventdriven.stresstest.core.service.UpdateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

/*
 * Stress test controller
 *
 * @author joseluis.nogueira on 11/10/2019
 */
@Log4j2
@RestController
@RequestMapping(value = "/stress-test")
@Api(
  value = "Stress test",
  tags = {"Stress test"}
)
public class MainController {
  @Autowired
  private CreationService creationService;
  @Autowired
  private UpdateService updateService;
  @Autowired
  private DeleteService deleteService;
  @Autowired
  private ComparatorService comparatorService;
  @Autowired
  private CleanService cleanService;

  @PostMapping(path = "/create/owners/{elements}", produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<String> createOwners(
    @ApiParam(value = "elements", required = true) @PathVariable(value = "elements") int elements) {
    if (elements <= 0) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    long t = System.currentTimeMillis();
    creationService.createOwners(elements);
    return ResponseEntity.ok("Create " + elements + " owners in " + printTime(System.currentTimeMillis() - t));
  }

  @PostMapping(path = "/update/owners/{elements}/{parallel}", produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<String> updateOwners(
    @ApiParam(value = "elements", required = true) @PathVariable(value = "elements") int elements,
    @ApiParam(value = "parallel", required = false) @PathVariable(value = "parallel") boolean parallel) {
    if (elements <= 0) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    long t = System.currentTimeMillis();
    updateService.updateOwners(elements, parallel);
    return ResponseEntity.ok("Updated " + elements + " owners in " + printTime(System.currentTimeMillis() - t));
  }

  @PostMapping(path = "/delete/owners/{elements}", produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<String> deleteOwners(
    @ApiParam(value = "elements", required = true) @PathVariable(value = "elements") int elements) {
    if (elements <= 0) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    long t = System.currentTimeMillis();
    int deleted = deleteService.deleteOwners(elements);
    return ResponseEntity.ok("Deleted " + deleted + " owners in " + printTime(System.currentTimeMillis() - t));
  }

  @PostMapping(path = "/create/cars/{elements}", produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<String> createCars(
    @ApiParam(value = "elements", required = true) @PathVariable(value = "elements") int elements) {
    if (elements <= 0) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    long t = System.currentTimeMillis();
    creationService.createCars(elements);
    return ResponseEntity.ok("Create " + elements + " cars in " + printTime(System.currentTimeMillis() - t));
  }

  @PostMapping(path = "/update/cars/{elements}/{parallel}", produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<String> updateCars(
    @ApiParam(value = "elements", required = true) @PathVariable(value = "elements") int elements,
    @ApiParam(value = "parallel", required = false) @PathVariable(value = "parallel") boolean parallel) {
    if (elements <= 0) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    long t = System.currentTimeMillis();
    updateService.updateCars(elements, parallel);
    return ResponseEntity.ok("Updated " + elements + " cars in " + printTime(System.currentTimeMillis() - t));
  }

  @PostMapping(path = "/delete/cars/{elements}", produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<String> deleteCars(
    @ApiParam(value = "elements", required = true) @PathVariable(value = "elements") int elements) {
    if (elements <= 0) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    long t = System.currentTimeMillis();
    int deleted = deleteService.deleteCars(elements);
    return ResponseEntity.ok("Deleted " + deleted + " cars in " + printTime(System.currentTimeMillis() - t));
  }

  @PostMapping(path = "/comparator", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<String>> comparator() {
    return ResponseEntity.ok(comparatorService.run());
  }

  @PostMapping(path = "/clean", produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<Void> clean() {
    cleanService.run();
    return ResponseEntity.ok().build();
  }

  private String printTime(long millis) {
    return String.format("%d min, %d sec",
      TimeUnit.MILLISECONDS.toMinutes(millis),
      TimeUnit.MILLISECONDS.toSeconds(millis) -
        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
  }

}
