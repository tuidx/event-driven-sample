package com.tui.architecture.eventdriven.query.core.event;

import com.tui.architecture.eventdriven.query.core.service.enums.EEventOperation;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/*
 * Message event
 *
 * @author joseluis.nogueira on 28/08/2019
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public class DataModifiedEvent extends ApplicationEvent {
  private final String mediaType;
  private final EEventOperation eEventOperation;

  public DataModifiedEvent(String mediaType, Object source, EEventOperation eEventOperation) {
    super(source);
    this.mediaType = mediaType;
    this.eEventOperation = eEventOperation;
  }
}
