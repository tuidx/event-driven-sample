package com.tui.architecture.eventdriven.query.dto.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/*
 * DTO for Car data
 *
 * @author joseluis.nogueira on 28/08/2019
 */
@Data
@JsonTypeName("car")
@ApiModel(value = "Car", description = "Car information")
public class CarDTO {
  public static final String MEDIA_TYPE = "application/vnd.car.v1+json";

  @NotNull
  @Size(max = 20)
  private String registration;

  @NotNull
  @Size(max = 20)
  private String owner;

  @NotNull
  @Size(max = 30)
  private String brand;

  @NotNull
  @Size(max = 60)
  private String model;

  @NotNull
  @Size(max = 40)
  private String color;

  @Min(1950)
  @Max(2100)
  private Integer year;
}
