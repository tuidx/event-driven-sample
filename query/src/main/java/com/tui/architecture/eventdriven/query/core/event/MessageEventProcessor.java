package com.tui.architecture.eventdriven.query.core.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tui.architecture.eventdriven.query.core.service.ICrudService;
import com.tui.architecture.eventdriven.query.exception.EventException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/*
 * Spring Messages processor
 *
 * @author joseluis.nogueira on 17/10/2019
 */
@Slf4j
@Component
public class MessageEventProcessor { // implements ApplicationListener<DataModifiedEvent> {
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private HashMap<String, ICrudService> crudServices;

  @EventListener
  public void dataModifiedEventHandler(DataModifiedEvent dataModifiedEvent) {
    ICrudService crudService = crudServices.get(dataModifiedEvent.getMediaType());
    Object dto;
    try {
      Object json = dataModifiedEvent.getSource();
      dto = json instanceof String ? objectMapper.readValue((String) json, crudService.getDtoClass()) : objectMapper.readValue((byte[]) json, crudService.getDtoClass());
    } catch (Exception e) {
      log.error("Serialization exception. Object {}", dataModifiedEvent.getSource(), e);
      return;
    }

    try {
      switch (dataModifiedEvent.getEEventOperation()) {
        case CREATED:
          crudService.create(dto);
          break;
        case UPDATED:
          crudService.update(dto);
          break;
        case DELETED:
          crudService.delete(dto);
          break;
        default:
          throw new EventException("Not implemented action for header media-type: " + dataModifiedEvent.getMediaType());
      }
    } catch (EventException e){
      log.error("Error processing DataModifiedEvent", e);
    }
  }
}
