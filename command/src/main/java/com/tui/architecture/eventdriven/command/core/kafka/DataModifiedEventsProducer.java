package com.tui.architecture.eventdriven.command.core.kafka;

import com.tui.architecture.eventdriven.command.core.event.DataModifiedEvent;
import com.tui.architecture.eventdriven.command.core.kafka.configuration.EventChannels;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.core.env.Environment;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;

/*
 * Produccer messages for Kafka
 *
 * @author joseluis.nogueira on 09/10/2019
 */
@Slf4j
@Component
@EnableBinding(EventChannels.class)
public class DataModifiedEventsProducer {
  @Autowired
  private EventChannels eventChannels;
  @Autowired
  private KafkaTemplate kafkaTemplate;
  @Autowired
  private Environment environment;

  private String destination;

  @PostConstruct
  public void setUp() {
    destination = environment.getProperty("spring.cloud.stream.bindings." + EventChannels.DATA_MODIFIED_EVENT_CHANNEL + ".destination");
  }

  public boolean sendDataModifiedEvent(DataModifiedEvent dataModifiedEvent) {
    boolean result;
    if (dataModifiedEvent.getSource() != KafkaNull.INSTANCE) {
      Message message = MessageBuilder.
        withPayload(dataModifiedEvent.getSource()).
        copyHeaders(dataModifiedEvent.getHeaders()).
        build();
      if (log.isDebugEnabled()) {
        log.debug("[KAFKA] Event notification {}", message);
      }
      result = eventChannels.dataModifiedEventChannel().send(message);
    } else {
      result = sendPayloadNull(dataModifiedEvent);
    }
    return result;
  }

  private boolean sendPayloadNull(DataModifiedEvent dataModifiedEvent) {
    if (log.isDebugEnabled()) {
      log.debug("Send key = {} with null value", dataModifiedEvent.getHeaders().get(KafkaHeaders.MESSAGE_KEY));
    }
    ListenableFuture listenableFuture = kafkaTemplate.send(destination, dataModifiedEvent.getHeaders().get(KafkaHeaders.MESSAGE_KEY), null);
    boolean result;
    try {
      listenableFuture.completable().get();
      result = listenableFuture.isDone();
    } catch (ExecutionException e) {
      log.error("Error sending message", e);
      result = false;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      result = false;
    }
    return result;
  }
}
