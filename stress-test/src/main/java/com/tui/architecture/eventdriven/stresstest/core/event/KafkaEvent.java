package com.tui.architecture.eventdriven.stresstest.core.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.kafka.support.KafkaNull;

import java.util.HashMap;
import java.util.Map;

/*
 * Abstract class for events sent by Kafka
 *
 * @author joseluis.nogueira on 08/10/2019
 */
public abstract class KafkaEvent extends ApplicationEvent {
  private final Map<String, Object> headers;

  public KafkaEvent(Object source) {
    super(source != null ? source : KafkaNull.INSTANCE);
    this.headers = new HashMap<>();
  }

  protected void addHeader(String key, Object value){
    headers.put(key, value);
  }

  public Map<String, Object> getHeaders(){
    return new HashMap<>(headers);
  }
}
