package com.tui.architecture.eventdriven.command.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/*
 * DTO for Car data
 *
 * @author joseluis.nogueira on 28/08/2019
 */
@Data
@JsonTypeName("car")
@ApiModel(value = "Car", description = "Car information")
@XmlRootElement
public class CarDTO {
  public static final String MEDIA_TYPE = "application/vnd.car.v1+json";

  @NotNull
  @Size(max = 20)
  @ApiModelProperty(position = 1)
  private String registration;

  @NotNull
  @Size(max = 20)
  @ApiModelProperty(position = 2)
  private String owner;

  @NotNull
  @Size(max = 30)
  @ApiModelProperty(position = 3)
  private String brand;

  @NotNull
  @Size(max = 60)
  @ApiModelProperty(position = 4)
  private String model;

  @NotNull
  @Size(max = 40)
  @ApiModelProperty(position = 5)
  private String color;

  @Min(1950)
  @Max(2100)
  @ApiModelProperty(position = 6)
  private Integer year;
}
