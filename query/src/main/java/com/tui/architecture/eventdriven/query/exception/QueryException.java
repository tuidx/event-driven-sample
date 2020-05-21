package com.tui.architecture.eventdriven.query.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * 
 * @author amonterop
 *
 */
@Data
public class QueryException extends Exception {
  private final HttpStatus httpStatus;

  public QueryException(HttpStatus httpStatus){
    this.httpStatus = httpStatus;
  }

  public QueryException(HttpStatus httpStatus, Throwable cause) {
    super(cause);
    this.httpStatus = httpStatus;
  }
}
