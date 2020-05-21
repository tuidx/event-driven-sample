package com.tui.architecture.eventdriven.stresstest.db.repository;

import com.tui.architecture.eventdriven.stresstest.db.entity.OwnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*
 * Repository for owner entity
 *
 * @author joseluis.nogueira on 28/08/2019
 */
@Repository
public interface OwnerRepository extends JpaRepository<OwnerEntity, String> {
}
