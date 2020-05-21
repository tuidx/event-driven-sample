package com.tui.architecture.eventdriven.query.core.service;

import com.tui.architecture.eventdriven.query.exception.EventException;

import java.lang.reflect.ParameterizedType;

/*
 *
 *
 * @author joseluis.nogueira on 10/09/2019
 */
public interface ICrudService<T> {
  void create(T dto) throws EventException;

  void update(T dto) throws EventException;

  void delete(T dto) throws EventException;

  default Class<T> getDtoClass(){
    return (Class<T>) ((ParameterizedType) this.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
  }
}
