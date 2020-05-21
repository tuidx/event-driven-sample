package com.tui.architecture.eventdriven.query.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.tui.architecture.eventdriven.query.dto.event.CarDTO;
import com.tui.architecture.eventdriven.query.dto.event.OwnerDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/*
 * Response for search by owner
 *
 * @author joseluis.nogueira on 11/09/2019
 */
@Data
@JsonTypeName("owner")
@ApiModel(value = "Owner", description = "Owner information")
@XmlRootElement
public class ResponseOwnerDTO extends OwnerDTO {
  public static final String MEDIA_TYPE_HEADER = "application/vnd.response-owner.v1+json";

  @JsonProperty(value = "cars")
  public List<CarDTO> carDTOS;
}
