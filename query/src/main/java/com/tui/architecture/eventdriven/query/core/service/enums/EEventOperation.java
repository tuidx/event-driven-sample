package com.tui.architecture.eventdriven.query.core.service.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
 * Actions for messages
 *
 * @author joseluis.nogueira on 28/08/2019
 */
public enum EEventOperation {
  CREATED("created"),
  UPDATED("updated"),
  DELETED("deleted");

  private static final Map<String, EEventOperation> reverse = Arrays.stream(EEventOperation.values()).collect(Collectors.toMap(EEventOperation::getOperation, Function.identity()));

  @Getter
  private final String operation;

  EEventOperation(String operation){
    this.operation = operation;
  }

  public static EEventOperation operationOf(String operation){
    return reverse.get(operation);
  }
}
