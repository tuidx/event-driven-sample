package com.tui.architecture.eventdriven.query.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.tui.architecture.eventdriven.query.dto.event.CarDTO;
import com.tui.architecture.eventdriven.query.dto.event.OwnerDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

/*
 * Response for search by owner
 *
 * @author joseluis.nogueira on 11/09/2019
 */
@Data
@JsonTypeName("car")
@JsonIgnoreProperties({"owner"})
@ApiModel(value = "Car", description = "Car information")
@XmlRootElement
public class ResponseCarDTO extends CarDTO {
  public static final String MEDIA_TYPE_HEADER = "application/vnd.response-car.v1+json";

  @JsonProperty(value = "car_owner")
  private OwnerDTO ownerDTO;
}
