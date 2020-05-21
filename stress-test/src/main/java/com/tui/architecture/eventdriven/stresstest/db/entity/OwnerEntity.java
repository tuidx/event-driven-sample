package com.tui.architecture.eventdriven.stresstest.db.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Owner entity
 *
 * @author joseluis.nogueira on 28/08/2019
 */
@Entity
@Table(name = "owner")
@Setter
@Getter
@EqualsAndHashCode
public class OwnerEntity {
  @Id
  @Column
  @NotNull
  private String id;

  @NotNull
  @Column
  private String name;

  @NotNull
  @Column
  private String surname;

  @Column
  private Integer age;
}
