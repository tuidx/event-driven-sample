package com.tui.architecture.eventdriven.command.core.event;

import lombok.EqualsAndHashCode;
import org.springframework.kafka.support.KafkaHeaders;

/*
 * Message event
 *
 * @author joseluis.nogueira on 28/08/2019
 */
@EqualsAndHashCode(callSuper = false)
public class DataModifiedEvent extends KafkaEvent {
  public static final String MEDIA_TYPE = "media-type";
  public static final String OPERATION = "operation";

  public DataModifiedEvent(String key, String mediaType, Object source, EEventOperation eEventOperation) {
    super(source);
    addHeader(MEDIA_TYPE, mediaType);
    addHeader(OPERATION, eEventOperation.getOperation());
    addHeader(KafkaHeaders.MESSAGE_KEY, key.getBytes());
  }

  public String getKey(){
    return new String((byte[]) getHeaders().get(KafkaHeaders.MESSAGE_KEY));
  }

  public EEventOperation getOperation(){
    return EEventOperation.operarationOf((String) getHeaders().get(OPERATION));
  }

}
