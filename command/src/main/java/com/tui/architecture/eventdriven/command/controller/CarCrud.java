package com.tui.architecture.eventdriven.command.controller;

import com.tui.architecture.eventdriven.command.core.service.CarService;
import com.tui.architecture.eventdriven.command.dto.CarDTO;
import com.tui.architecture.eventdriven.command.exception.ServiceException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.CACHE_CONTROL;

/*
 * Controller for car entity commands
 *
 * @author joseluis.nogueira on 28/08/2019
 */
@Log4j2
@RestController
@RequestMapping(value = "/car-service")
@Api(
        value = "Car Entity",
        tags = {"Car Entity"}
)
public class CarCrud {
  @Autowired
  private CarService carService;

  private static final String NO_CACHE = "no store, private, max-age=0";

  /**
   * Create a new car.
   *
   * @param carDTO        {@link CarDTO}
   * @param bindingResult {@link BindingResult}
   * @return ResponseEntity {@link ResponseEntity}
   */
  @ApiOperation(
          tags = "Car Entity",
          value = "Create a new car.",
          notes = "Create a new car."
  )
  @ApiResponses(
          value = {
                  @ApiResponse(code = 201, message = "Created - The request was successful, we created a new resource and the response body contains the representation."),
                  @ApiResponse(code = 400, message = "Bad Request - The data given in the POST failed validation. Inspect the response body for details."),
                  @ApiResponse(code = 408, message = "Request Timeout"),
                  @ApiResponse(code = 409, message = "Conflict - The request could not be processed because of conflict in the request"),
                  @ApiResponse(code = 429, message = "Too Many Requests - Your application is sending too many simultaneous requests."),
                  @ApiResponse(code = 500, message = "Internal Server Error - We couldn't create the resource. Please try again."),
                  @ApiResponse(code = 503, message = "Service Unavailable - We are temporarily unable. Please wait for a bit and try again. ")
          }
  )
  @PostMapping(path = "/cars", produces = CarDTO.MEDIA_TYPE)
  public ResponseEntity<CarDTO> create(@ApiParam(value = "car", name = "car", required = true) @Valid @RequestBody CarDTO carDTO, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      log.warn("Conflict", bindingResult.getAllErrors());
      return ResponseEntity.badRequest().build();
    }

    CarDTO response;
    try {
      response = carService.create(carDTO);
    } catch (ServiceException e) {
      log.warn(e.getHttpStatus().getReasonPhrase(), e.getCause());
      return ResponseEntity.status(e.getHttpStatus()).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    return ResponseEntity.status(HttpStatus.CREATED).header(CACHE_CONTROL, NO_CACHE).body(response);
  }

  /**
   * Update a car by id.
   *
   * @param carDTO        {@link CarDTO}
   * @param bindingResult {@link BindingResult}
   * @return Void {@link Void}
   */
  @ApiOperation(
          tags = "Car Entity",
          value = "Update a car by id.",
          notes = "Update a car by id."
  )
  @ApiResponses(
          value = {
                  @ApiResponse(code = 200, message = "OK - The request was successful, we updated the resource and the response body contains the representation."),
                  @ApiResponse(code = 204, message = "No Content - The request was successful, we created a new resource and the response body does not contains the representation."),
                  @ApiResponse(code = 400, message = "Bad Request - The data given in the PUT failed validation. Inspect the response body for details."),
                  @ApiResponse(code = 401, message = "Unauthorized - The supplied credentials, if any, are not sufficient to access the resource."),
                  @ApiResponse(code = 408, message = "Request Timeout"),
                  @ApiResponse(code = 409, message = "Conflict - The request could not be processed because of conflict in the request"),
                  @ApiResponse(code = 429, message = "Too Many Requests - Your application is sending too many simultaneous requests."),
                  @ApiResponse(code = 500, message = "Internal Server Error - We couldn't create the resource. Please try again."),
                  @ApiResponse(code = 503, message = "Service Unavailable - We are temporarily unable. Please wait for a bit and try again. ")
          }
  )
  @PutMapping(path = "/cars", produces = CarDTO.MEDIA_TYPE)
  public ResponseEntity<Void> update(
          @ApiParam(value = "registration", name = "registration", required = true) @Valid @RequestBody CarDTO carDTO,
          BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      log.warn("Conflict", bindingResult.getAllErrors());
      return ResponseEntity.badRequest().build();
    }

    try {
       carService.update(carDTO);
    } catch (ServiceException e) {
      log.warn(e.getHttpStatus().getReasonPhrase(), e.getCause());
      return ResponseEntity.status(e.getHttpStatus()).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    return ResponseEntity.noContent().header(CACHE_CONTROL, NO_CACHE).build();
  }

  /**
   * Delete a car by id.
   *
   * @param registration {@link Integer}
   * @return ResponseEntity {@link ResponseEntity}
   */
  @ApiOperation(
          tags = "Car Entity",
          value = "Delete a car by id.",
          notes = "Delete a car by id."
  )
  @ApiResponses(
          value = {
                  @ApiResponse(code = 204, message = "OK - The request was successful; the resource was deleted."),
                  @ApiResponse(code = 401, message = "Unauthorized - The supplied credentials, if any, are not sufficient to access the resource."),
                  @ApiResponse(code = 404, message = "Not Found"),
                  @ApiResponse(code = 408, message = "Request Timeout"),
                  @ApiResponse(code = 429, message = "Too Many Requests - Your application is sending too many simultaneous requests."),
                  @ApiResponse(code = 500, message = "Internal Server Error - We couldn't delete the resource. Please try again."),
                  @ApiResponse(code = 503, message = "Service Unavailable")
          }
  )
  @DeleteMapping(path = "/cars/{registration}", produces = CarDTO.MEDIA_TYPE)
  public ResponseEntity<Void> delete(@ApiParam(value = "registration", required = true) @PathVariable(value = "registration") String registration) {
    try {
      carService.delete(registration);
    } catch (ServiceException e) {
      log.warn(e.getHttpStatus().getReasonPhrase(), e.getCause());
      return ResponseEntity.status(e.getHttpStatus()).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    return ResponseEntity.ok().header(CACHE_CONTROL, NO_CACHE).build();

  }

}
