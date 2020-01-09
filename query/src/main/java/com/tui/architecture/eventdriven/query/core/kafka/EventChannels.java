package com.tui.architecture.eventdriven.query.core.kafka;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/*
 * Configuration of kafka channels
 *
 * @author joseluis.nogueira on 28/08/2019
 */
public interface EventChannels {
  String DATA_MODIFIED_EVENT_CHANNEL = "data_modified_event_channel";

  @Input(DATA_MODIFIED_EVENT_CHANNEL)
  SubscribableChannel dataModifiedEvent();
}
