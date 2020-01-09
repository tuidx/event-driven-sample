package com.tui.architecture.eventdriven.stresstest.core.event;

import lombok.Getter;

/*
 * Actions for messages
 *
 * @author joseluis.nogueira on 28/08/2019
 */
public enum EEventOperation {
  CREATED("created"),
  UPDATED("updated"),
  DELETED("deleted");

  @Getter
  private final String operation;

  EEventOperation(String operation){
    this.operation = operation;
  }
}
