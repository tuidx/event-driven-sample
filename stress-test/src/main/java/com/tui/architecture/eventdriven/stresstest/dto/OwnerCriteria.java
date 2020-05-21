package com.tui.architecture.eventdriven.stresstest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/*
 * Criteria for filter owners
 *
 * @author joseluis.nogueira on 11/09/2019
 */
@Data
@JsonTypeName("ownerCriteria")
public class OwnerCriteria {
  @ApiModelProperty(position = 1)
  private String id;
  @ApiModelProperty(position = 2)
  private String name;
  @ApiModelProperty(position = 3)
  private String surname;
  @JsonProperty(value = "min_age")
  @ApiModelProperty(position = 4)
  private Integer minAge;
  @JsonProperty(value = "max_age")
  @ApiModelProperty(position = 5)
  private Integer maxAge;
}
