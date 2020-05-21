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
 * DTO for Owner data
 *
 * @author joseluis.nogueira on 28/08/2019
 */
@Data
@JsonTypeName("owner")
@ApiModel(value = "Owner", description = "Owner information")
@XmlRootElement
public class OwnerDTO {
  public static final String MEDIA_TYPE = "application/vnd.owner.v1+json";

  @NotNull
  @Size(max = 20)
  @ApiModelProperty(position = 1)
  private String id;

  @NotNull
  @Size(max = 100)
  @ApiModelProperty(position = 2)
  private String name;

  @NotNull
  @Size(max = 100)
  @ApiModelProperty(position = 3)
  private String surname;

  @Min(12)
  @Max(130)
  @ApiModelProperty(position = 4)
  private Integer age;
}
