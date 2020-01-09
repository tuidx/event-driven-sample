package com.tui.architecture.eventdriven.query.core.kafka;

import com.tui.architecture.eventdriven.query.core.event.DataModifiedEvent;
import com.tui.architecture.eventdriven.query.core.service.enums.EEventOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

/*
 * Consumer topic synchronization.data-modified-events
 *
 * @author joseluis.nogueira on 10/09/2019
 */
@Slf4j
@Service
@EnableBinding(EventChannels.class)
public class DataModifiedEventsConsumer {
  public static final String MEDIA_TYPE_HEADER = "media-type";
  public static final String OPERATION_HEADER = "operation";

  @Autowired
  private ApplicationEventPublisher eventPublisher;

  @StreamListener(target = EventChannels.DATA_MODIFIED_EVENT_CHANNEL, condition = "headers['media-type'] != null")
  public void eventNotificacion(Message<?> message) {
    if (log.isDebugEnabled()) {
      log.debug("Topic event notification received: {}", message);
    }
    String mediaType = message.getHeaders().containsKey(MEDIA_TYPE_HEADER) ? message.getHeaders().get(MEDIA_TYPE_HEADER).toString() : null;
    EEventOperation operation = message.getHeaders().containsKey(OPERATION_HEADER) ? EEventOperation.operationOf(message.getHeaders().get(OPERATION_HEADER).toString()) : null;
    if (mediaType != null && operation != null) {
      eventPublisher.publishEvent(new DataModifiedEvent(mediaType, message.getPayload(), operation));
    }
  }
}
