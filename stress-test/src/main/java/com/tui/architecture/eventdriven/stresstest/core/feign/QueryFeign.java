package com.tui.architecture.eventdriven.stresstest.core.feign;

import com.tui.architecture.eventdriven.stresstest.dto.CarDTO;
import com.tui.architecture.eventdriven.stresstest.dto.OwnerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/*
 * Feign for attach to query service
 *
 * @author joseluis.nogueira on 11/10/2019
 */
@FeignClient(name = "query", url = "http://localhost:8090")
public interface QueryFeign {
  @GetMapping(path = "/raw-service/owners/{id}", produces = OwnerDTO.MEDIA_TYPE)
  ResponseEntity<OwnerDTO> retrieveOwner(@PathVariable(value = "id") String id);

  @GetMapping(path = "/raw-service/owners", produces = OwnerDTO.MEDIA_TYPE)
  ResponseEntity<List<OwnerDTO>> retrieveOwnerByCriteria();

  @GetMapping(path = "/raw-service/cars/{registration}", produces = CarDTO.MEDIA_TYPE)
  ResponseEntity<CarDTO> retrieveCar( @PathVariable(value = "registration") String registration);

  @GetMapping(path = "/raw-service/cars", produces = CarDTO.MEDIA_TYPE)
  ResponseEntity<List<CarDTO>> retrieveCarByCriteria();
}
