package com.tui.architecture.eventdriven.query.db.entity;

import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.attribute.SimpleNullableAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;
import com.tui.architecture.eventdriven.query.dto.event.CarDTO;
import com.tui.architecture.eventdriven.query.dto.event.OwnerDTO;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Memory element
 *
 * @author joseluis.nogueira on 11/09/2019
 */
@Data
@NoArgsConstructor
public class MemoryEntity {
  /* Indexes */
  public static final SimpleAttribute<MemoryEntity, String> ID = new SimpleAttribute<MemoryEntity, String>("id") {
    public String getValue(MemoryEntity entity, QueryOptions queryOptions) {
      return entity.id;
    }
  };
  public static final SimpleAttribute<MemoryEntity, String> NAME = new SimpleAttribute<MemoryEntity, String>("name") {
    public String getValue(MemoryEntity entity, QueryOptions queryOptions) {
      return entity.name;
    }
  };
  public static final SimpleAttribute<MemoryEntity, String> SURNAME = new SimpleAttribute<MemoryEntity, String>("surname") {
    public String getValue(MemoryEntity entity, QueryOptions queryOptions) {
      return entity.surname;
    }
  };
  public static final SimpleNullableAttribute<MemoryEntity, Integer> AGE = new SimpleNullableAttribute<MemoryEntity, Integer>("age") {
    public Integer getValue(MemoryEntity entity, QueryOptions queryOptions) {
      return entity.age;
    }
  };

  public static final SimpleNullableAttribute<MemoryEntity, String> REGISTRATION = new SimpleNullableAttribute<MemoryEntity, String>("registration") {
    public String getValue(MemoryEntity entity, QueryOptions queryOptions) { return entity.registration; }
  };
  public static final SimpleNullableAttribute<MemoryEntity, String> BRAND = new SimpleNullableAttribute<MemoryEntity, String>("brand") {
    public String getValue(MemoryEntity entity, QueryOptions queryOptions) { return entity.brand; }
  };
  public static final SimpleNullableAttribute<MemoryEntity, String> MODEL = new SimpleNullableAttribute<MemoryEntity, String>("model") {
    public String getValue(MemoryEntity entity, QueryOptions queryOptions) { return entity.model; }
  };
  public static final SimpleNullableAttribute<MemoryEntity, String> COLOR = new SimpleNullableAttribute<MemoryEntity, String>("color") {
    public String getValue(MemoryEntity entity, QueryOptions queryOptions) { return entity.color; }
  };
  public static final SimpleNullableAttribute<MemoryEntity, Integer> YEAR = new SimpleNullableAttribute<MemoryEntity, Integer>("year") {
    public Integer getValue(MemoryEntity entity, QueryOptions queryOptions) { return entity.year; }
  };

  /* Owner */
  protected String id;
  protected String name;
  protected String surname;
  protected Integer age;

  /* Car */
  protected String registration;
  protected String brand;
  protected String model;
  protected String color;
  protected Integer year;
}
