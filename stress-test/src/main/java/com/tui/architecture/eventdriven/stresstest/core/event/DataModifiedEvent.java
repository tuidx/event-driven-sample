package com.tui.architecture.eventdriven.stresstest.core.event;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.kafka.support.KafkaHeaders;

/*
 * Message event
 *
 * @author joseluis.nogueira on 28/08/2019
 */
@EqualsAndHashCode(callSuper = false)
@ToString
public class DataModifiedEvent extends KafkaEvent {
  public static final String MEDIA_TYPE_HEADER = "media-type";
  public static final String OPERATION_HEADER = "operation";

  public DataModifiedEvent(String key, String mediaType, Object source, EEventOperation eEventOperation) {
    super(source);
    addHeader(MEDIA_TYPE_HEADER, mediaType);
    addHeader(OPERATION_HEADER, eEventOperation.getOperation());
    addHeader(KafkaHeaders.MESSAGE_KEY, key.getBytes());
  }
}
