package com.tui.architecture.eventdriven.query.exception;

import lombok.Data;

/**
 * Generic Exception for notifications received by kafka
 *
 * @author joseluis.nogueira on 10/09/2019
 */
@Data
public class EventException extends Exception {
  public EventException(Throwable cause) {
    super(cause);
  }

  public EventException(String message){
    super(message);
  }
}
