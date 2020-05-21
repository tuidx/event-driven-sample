package com.tui.architecture.eventdriven.stresstest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/*
 * Criteria for filter car
 *
 * @author joseluis.nogueira on 12/09/2019
 */
@Data
@JsonTypeName("carCriteria")
public class CarCriteria {
  @ApiModelProperty(position = 1)
  private String registration;
  @ApiModelProperty(position = 2)
  private String brand;
  @ApiModelProperty(position = 3)
  private String model;
  @ApiModelProperty(position = 4)
  private String color;
  @JsonProperty(value = "min_year")
  @ApiModelProperty(position = 5)
  private Integer minYear;
  @JsonProperty(value = "max_year")
  @ApiModelProperty(position = 6)
  private Integer maxYear;
}
