package com.tui.architecture.eventdriven.stresstest.core.kafka.configuration;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/*
 * Configuration of kafka channels
 *
 * @author joseluis.nogueira on 28/08/2019
 */
public interface EventChannels {
  String DATA_MODIFIED_EVENT_CHANNEL = "data_modified_event_channel";

  @Output(DATA_MODIFIED_EVENT_CHANNEL)
  MessageChannel dataModifiedEventChannel();
}
