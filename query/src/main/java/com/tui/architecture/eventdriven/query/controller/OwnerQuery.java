package com.tui.architecture.eventdriven.query.controller;

import com.googlecode.cqengine.resultset.ResultSet;
import com.tui.architecture.eventdriven.query.core.mapper.MemoryEntityMapper;
import com.tui.architecture.eventdriven.query.db.entity.MemoryEntity;
import com.tui.architecture.eventdriven.query.db.repository.MemoryRepository;
import com.tui.architecture.eventdriven.query.dto.OwnerCriteria;
import com.tui.architecture.eventdriven.query.dto.event.OwnerDTO;
import com.tui.architecture.eventdriven.query.dto.response.ResponseOwnerDTO;
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
 * Controller for queries by owner
 *
 * @author joseluis.nogueira on 28/08/2019
 */
@Log4j2
@RestController
@RequestMapping(value = "/owner-service")
@Api(
        value = "Owner Entity",
        tags = {"Owner Entity"}
)
public class OwnerQuery {
  @Autowired
  private MemoryRepository memoryRepository;

  @Autowired
  private MemoryEntityMapper memoryEntityMapper;

  /**
   * Get a owner by id.
   *
   * @param id
   * @return ResponseEntity {@link ResponseEntity}
   */
  @ApiOperation(
          tags = "Owner Entity",
          value = "Get a Owner by id.",
          notes = "Get a Owner by id."
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
  public ResponseEntity<ResponseOwnerDTO> retrieve(
          @ApiParam(value = "id", required = true) @PathVariable(value = "id") String id) {
    ResponseEntity responseEntity;
    try {
      ResultSet<MemoryEntity> memoryEntities = memoryRepository.findOwnerById(id);
      if (memoryEntities.isNotEmpty()){
        List<ResponseOwnerDTO> responseOwnerDTOS = memoryEntityMapper.toResponseOwnerDTO(memoryEntities.stream().collect(Collectors.toList()));
        if (responseOwnerDTOS.size() == 1){
          responseEntity = ResponseEntity.ok(responseOwnerDTOS.get(0));
        } else {
          responseEntity = ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
      } else {
        responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      }
    } catch (Exception e){
      responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    return responseEntity;
  }

  /**
   * Get a car by some criteria.
   *
   * @param ownerCriteria
   * @return ResponseEntity {@link ResponseEntity}
   */
  @ApiOperation(
          tags = "Owner Entity",
          value = "Get a Owner by id.",
          notes = "Get a Owner by id."
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
  public ResponseEntity<List<ResponseOwnerDTO>> retrieveByCriteria(
          @ApiParam(value = "criteria") OwnerCriteria ownerCriteria) {
    ResponseEntity responseEntity;
    try {
      ResultSet<MemoryEntity> memoryEntities = memoryRepository.findOwnerByCriteria(ownerCriteria);
      responseEntity = ResponseEntity.ok(memoryEntityMapper.toResponseOwnerDTO(memoryEntities.stream().collect(Collectors.toList())));
    } catch (Exception e){
      responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    return responseEntity;
  }

}
