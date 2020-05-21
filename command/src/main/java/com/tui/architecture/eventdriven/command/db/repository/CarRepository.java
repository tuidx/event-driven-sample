package com.tui.architecture.eventdriven.command.db.repository;

import com.tui.architecture.eventdriven.command.db.entity.CarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * Repository for car entity
 *
 * @author joseluis.nogueira on 28/08/2019
 */
@Repository
public interface CarRepository extends JpaRepository<CarEntity, String> {
  List<CarEntity> findByOwner(String owner);
}
