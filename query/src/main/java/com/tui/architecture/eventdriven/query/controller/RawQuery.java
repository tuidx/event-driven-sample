package com.tui.architecture.eventdriven.query.controller;

import com.googlecode.cqengine.resultset.ResultSet;
import com.tui.architecture.eventdriven.query.core.mapper.MemoryEntityMapper;
import com.tui.architecture.eventdriven.query.db.entity.MemoryEntity;
import com.tui.architecture.eventdriven.query.db.repository.MemoryRepository;
import com.tui.architecture.eventdriven.query.dto.CarCriteria;
import com.tui.architecture.eventdriven.query.dto.OwnerCriteria;
import com.tui.architecture.eventdriven.query.dto.event.CarDTO;
import com.tui.architecture.eventdriven.query.dto.event.OwnerDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/*
 * Return raw data
 *
 * @author joseluis.nogueira on 11/10/2019
 */
@Log4j2
@RestController
@RequestMapping(value = "/raw-service")
@Api(
  value = "Raw Entities",
  tags = {"Raw Entities"}
)
public class RawQuery {
  @Autowired
  private MemoryRepository memoryRepository;

  @Autowired
  private MemoryEntityMapper memoryEntityMapper;

  @ApiOperation(
    value = "Get a Raw Owner by id.",
    notes = "Get a Raw Owner by id."
  )
  @ApiResponses(
    value = {
      @ApiResponse(code = 200, message = "OK - The request was successful and the response body contains the representation requested."),
      @ApiResponse(code = 400, message = "Bad Request - The data given in the GET failed validation. Inspect the response body for details."),
      @ApiResponse(code = 404, message = "Not Found"),
      @ApiResponse(code = 408, message = "Request Timeout"),
      @ApiResponse(code = 429, message = "Too Many Requests - Your application is sending too many simultaneous requests."),
      @ApiResponse(code = 500, message = "Internal Server Error - We couldn't return the representation due to an internal server error."),
      @ApiResponse(code = 503, message = "Service Unavailable - We are temporarily unable to return the representation. Please wait for a bit and try again."),
    }
  )
  @GetMapping(path = "/owners/{id}", produces = OwnerDTO.MEDIA_TYPE)
  public ResponseEntity<OwnerDTO> retrieveOwner(
    @ApiParam(value = "id", required = true) @PathVariable(value = "id") String id) {
    ResponseEntity responseEntity;
    try {
      ResultSet<MemoryEntity> memoryEntities = memoryRepository.findOwnerById(id);
      if (memoryEntities.isNotEmpty()) {
        responseEntity = ResponseEntity.ok(memoryEntityMapper.toOwnerDTO(memoryEntities.stream().findFirst().get()));
      } else {
        responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      }
    } catch (Exception e) {
      responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    return responseEntity;
  }

  @ApiOperation(
    value = "Get a Raw Owner by id.",
    notes = "Get a Raw Owner by id."
  )
  @ApiResponses(
    value = {
      @ApiResponse(code = 200, message = "OK - The request was successful and the response body contains the representation requested."),
      @ApiResponse(code = 400, message = "Bad Request - The data given in the GET failed validation. Inspect the response body for details."),
      @ApiResponse(code = 404, message = "Not Found"),
      @ApiResponse(code = 408, message = "Request Timeout"),
      @ApiResponse(code = 429, message = "Too Many Requests - Your application is sending too many simultaneous requests."),
      @ApiResponse(code = 500, message = "Internal Server Error - We couldn't return the representation due to an internal server error."),
      @ApiResponse(code = 503, message = "Service Unavailable - We are temporarily unable to return the representation. Please wait for a bit and try again."),
    }
  )
  @GetMapping(path = "/owners", produces = OwnerDTO.MEDIA_TYPE)
  public ResponseEntity<List<OwnerDTO>> retrieveOwnerByCriteria(
    @ApiParam(value = "id") @PathVariable(value = "id", required = false) String id,
    @ApiParam(value = "name") @PathVariable(value = "name", required = false) String name,
    @ApiParam(value = "surname") @PathVariable(value = "surname", required = false) String surname,
    @ApiParam(value = "min_age") @PathVariable(value = "min_age", required = false) Integer minAge,
    @ApiParam(value = "max_age") @PathVariable(value = "max_age", required = false) Integer maxAge) {
    ResponseEntity responseEntity;
    OwnerCriteria ownerCriteria = new OwnerCriteria();
    ownerCriteria.setId(id);
    ownerCriteria.setName(name);
    ownerCriteria.setSurname(surname);
    ownerCriteria.setMinAge(minAge);
    ownerCriteria.setMaxAge(maxAge);
    try {
      ResultSet<MemoryEntity> memoryEntities = memoryRepository.findOwnerByCriteria(ownerCriteria);
      responseEntity = ResponseEntity.ok(memoryEntities.
        stream().collect(Collectors.groupingBy(MemoryEntity::getId)).values().
        stream().map(e -> memoryEntityMapper.toOwnerDTO(e.get(0))).collect(Collectors.toList()));
    } catch (Exception e) {
      responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    return responseEntity;
  }

  /**
   * Get a owner by registration
   *
   * @param registration
   * @return ResponseEntity {@link ResponseEntity}
   */
  @ApiOperation(
    value = "Get a Raw Car by registration.",
    notes = "Get a Raw Car by registration."
  )
  @ApiResponses(
    value = {
      @ApiResponse(code = 200, message = "OK - The request was successful and the response body contains the representation requested."),
      @ApiResponse(code = 400, message = "Bad Request - The data given in the GET failed validation. Inspect the response body for details."),
      @ApiResponse(code = 404, message = "Not Found"),
      @ApiResponse(code = 408, message = "Request Timeout"),
      @ApiResponse(code = 429, message = "Too Many Requests - Your application is sending too many simultaneous requests."),
      @ApiResponse(code = 500, message = "Internal Server Error - We couldn't return the representation due to an internal server error."),
      @ApiResponse(code = 503, message = "Service Unavailable - We are temporarily unable to return the representation. Please wait for a bit and try again."),
    }
  )
  @GetMapping(path = "/cars/{registration}", produces = CarDTO.MEDIA_TYPE)
  public ResponseEntity<CarDTO> retrieveCar(
    @ApiParam(value = "registration", required = true) @PathVariable(value = "registration") String registration) {
    ResponseEntity responseEntity;
    try {
      ResultSet<MemoryEntity> memoryEntities = memoryRepository.findCarByRegistration(registration);
      if (memoryEntities.isNotEmpty()) {
        responseEntity = ResponseEntity.ok(memoryEntityMapper.toCarDTO(memoryEntities.stream().findFirst().get()));
      } else {
        responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      }
    } catch (Exception e) {
      responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    return responseEntity;
  }

  /**
   * Get a owner by some criteria.
   *
   * @param registration
   * @param brand
   * @param model
   * @param color
   * @param minYear
   * @param maxYear
   * @return
   */
  @ApiOperation(
    value = "Get a Raw Car by id.",
    notes = "Get a Raw Car by id."
  )
  @ApiResponses(
    value = {
      @ApiResponse(code = 200, message = "OK - The request was successful and the response body contains the representation requested."),
      @ApiResponse(code = 400, message = "Bad Request - The data given in the GET failed validation. Inspect the response body for details."),
      @ApiResponse(code = 404, message = "Not Found"),
      @ApiResponse(code = 408, message = "Request Timeout"),
      @ApiResponse(code = 429, message = "Too Many Requests - Your application is sending too many simultaneous requests."),
      @ApiResponse(code = 500, message = "Internal Server Error - We couldn't return the representation due to an internal server error."),
      @ApiResponse(code = 503, message = "Service Unavailable - We are temporarily unable to return the representation. Please wait for a bit and try again."),
    }
  )
  @GetMapping(path = "/cars", produces = CarDTO.MEDIA_TYPE)
  public ResponseEntity<List<CarDTO>> retrieveCarByCriteria(
    @ApiParam(value = "registration") @PathVariable(value = "registration", required = false) String registration,
    @ApiParam(value = "brand") @PathVariable(value = "brand", required = false) String brand,
    @ApiParam(value = "model") @PathVariable(value = "model", required = false) String model,
    @ApiParam(value = "color") @PathVariable(value = "color", required = false) String color,
    @ApiParam(value = "min_year") @PathVariable(value = "min_year", required = false) Integer minYear,
    @ApiParam(value = "max_year") @PathVariable(value = "max_year", required = false) Integer maxYear) {
    ResponseEntity responseEntity;
    CarCriteria carCriteria = new CarCriteria();
    carCriteria.setRegistration(registration);
    carCriteria.setBrand(brand);
    carCriteria.setModel(model);
    carCriteria.setColor(color);
    carCriteria.setMinYear(minYear);
    carCriteria.setMaxYear(maxYear);
    try {
      ResultSet<MemoryEntity> memoryEntities = memoryRepository.findCarByCriteria(carCriteria);
      responseEntity = ResponseEntity.ok(memoryEntities.stream().filter(e -> e.getRegistration() != null).map(e -> memoryEntityMapper.toCarDTO(e)).collect(Collectors.toList()));
    } catch (Exception e) {
      responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    return responseEntity;
  }

}
