package com.tui.architecture.eventdriven.stresstest.core.feign;

import com.tui.architecture.eventdriven.stresstest.dto.CarDTO;
import com.tui.architecture.eventdriven.stresstest.dto.OwnerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/*
 * Feign for attach to command service
 *
 * @author joseluis.nogueira on 11/10/2019
 */
@FeignClient(name = "command", url = "http://localhost:8080")
public interface CommandFeign {
  @PostMapping(path = "/owner-service/owners", produces = OwnerDTO.MEDIA_TYPE)
  ResponseEntity<OwnerDTO> createOwner(@Valid @RequestBody OwnerDTO ownerDTO);

  @PutMapping(path = "/owner-service/owners", produces = OwnerDTO.MEDIA_TYPE)
  ResponseEntity<Void> updateOwner(@Valid @RequestBody OwnerDTO ownerDTO);

  @DeleteMapping(path = "/owner-service/owners/{id}", produces = OwnerDTO.MEDIA_TYPE)
  ResponseEntity<Void> deleteOwner(@PathVariable(value = "id") String id);

  @PostMapping(path = "/car-service/cars", produces = CarDTO.MEDIA_TYPE)
  ResponseEntity<CarDTO> createCar(@Valid @RequestBody CarDTO carDTO);

  @PutMapping(path = "/car-service/cars", produces = CarDTO.MEDIA_TYPE)
  ResponseEntity<Void> updateCar(@Valid @RequestBody CarDTO carDTO);

  @DeleteMapping(path = "/car-service/cars/{registration}", produces = CarDTO.MEDIA_TYPE)
  ResponseEntity<Void> deleteCar(@PathVariable(value = "registration") String registration);
}
