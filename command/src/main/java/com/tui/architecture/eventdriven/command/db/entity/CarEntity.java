package com.tui.architecture.eventdriven.command.db.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Car entity
 *
 * @author joseluis.nogueira on 28/08/2019
 */
@Entity
@Table(name = "car")
@Setter
@Getter
@EqualsAndHashCode
public class CarEntity {
  @Id
  @Column
  @NotNull
  private String registration;

  @NotNull
  @Column
  private String owner;

  @NotNull
  @Column
  private String brand;

  @NotNull
  @Column
  private String model;

  @NotNull
  @Column
  private String color;

  @Column
  private Integer year;
}
