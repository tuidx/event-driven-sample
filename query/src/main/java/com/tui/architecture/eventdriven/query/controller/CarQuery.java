package com.tui.architecture.eventdriven.query.controller;

import com.googlecode.cqengine.resultset.ResultSet;
import com.tui.architecture.eventdriven.query.core.mapper.MemoryEntityMapper;
import com.tui.architecture.eventdriven.query.db.entity.MemoryEntity;
import com.tui.architecture.eventdriven.query.db.repository.MemoryRepository;
import com.tui.architecture.eventdriven.query.dto.CarCriteria;
import com.tui.architecture.eventdriven.query.dto.event.CarDTO;
import com.tui.architecture.eventdriven.query.dto.event.OwnerDTO;
import com.tui.architecture.eventdriven.query.dto.response.ResponseCarDTO;
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
 * Controller for queries by car
 *
 * @author joseluis.nogueira on 11/09/2019
 */
@Log4j2
@RestController
@RequestMapping(value = "/car-service")
@Api(
        value = "Car Entity",
        tags = {"Car Entity"}
)
public class CarQuery {
  @Autowired
  private MemoryRepository memoryRepository;

  @Autowired
  private MemoryEntityMapper memoryEntityMapper;

  /**
   * Get a owner by registration
   *
   * @param registration
   * @return ResponseEntity {@link ResponseEntity}
   */
  @ApiOperation(
          tags = "Car Entity",
          value = "Get a Car by registration.",
          notes = "Get a Car by registration."
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
  @GetMapping(path = "/cars/{registration}", produces = OwnerDTO.MEDIA_TYPE)
  public ResponseEntity<ResponseCarDTO> retrieve(
          @ApiParam(value = "registration", required = true) @PathVariable(value = "registration") String registration) {
    ResponseEntity responseEntity;
    try {
      ResultSet<MemoryEntity> memoryEntities = memoryRepository.findCarByRegistration(registration);
      if (memoryEntities.size() == 1){
        responseEntity = ResponseEntity.ok(memoryEntityMapper.toResponseCarDTO(memoryEntities.stream().collect(Collectors.toList())));
      } else if (memoryEntities.isEmpty()){
        responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      } else {
        responseEntity = ResponseEntity.status(HttpStatus.CONFLICT).build();
      }
    } catch (Exception e) {
      responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    return responseEntity;
  }

  /**
   * Get a owner by some criteria.
   *
   * @param carCriteria
   * @return ResponseEntity {@link ResponseEntity}
   */
  @ApiOperation(
          tags = "Car Entity",
          value = "Get a Car by id.",
          notes = "Get a Car by id."
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
  public ResponseEntity<List<ResponseCarDTO>> retrieveByCriteria(
          @ApiParam(value = "car_criteria") CarCriteria carCriteria) {
    ResponseEntity responseEntity;
    try {
      ResultSet<MemoryEntity> memoryEntities = memoryRepository.findCarByCriteria(carCriteria);
      responseEntity = ResponseEntity.ok(memoryEntityMapper.toResponseCarDTO(memoryEntities.stream().collect(Collectors.toList())));
    } catch (Exception e){
      responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    return responseEntity;
  }

}
