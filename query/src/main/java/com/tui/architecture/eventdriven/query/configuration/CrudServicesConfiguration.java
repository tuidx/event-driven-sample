package com.tui.architecture.eventdriven.query.configuration;

import com.tui.architecture.eventdriven.query.core.service.CarService;
import com.tui.architecture.eventdriven.query.core.service.ICrudService;
import com.tui.architecture.eventdriven.query.core.service.OwnerService;
import com.tui.architecture.eventdriven.query.dto.event.CarDTO;
import com.tui.architecture.eventdriven.query.dto.event.OwnerDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/*
 * Configuration with all CRUD services
 *
 * @author joseluis.nogueira on 10/09/2019
 */
@Configuration
public class CrudServicesConfiguration {
  @Bean
  public HashMap<String, ICrudService> crudServices(OwnerService ownerService, CarService carService){
    Map<String, ICrudService> map = new HashMap<>();
    map.put(OwnerDTO.MEDIA_TYPE, ownerService);
    map.put(CarDTO.MEDIA_TYPE, carService);
    return (HashMap<String, ICrudService>) map;
  }
}
