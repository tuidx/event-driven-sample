package com.tui.architecture.eventdriven.command.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * 
 * @author amonterop
 *
 */
@Data
public class ServiceException extends Exception {
  private final HttpStatus httpStatus;

  public ServiceException(HttpStatus httpStatus){
    this.httpStatus = httpStatus;
  }

  public ServiceException(HttpStatus httpStatus, Throwable cause) {
    super(cause);
    this.httpStatus = httpStatus;
  }
}
