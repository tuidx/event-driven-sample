package com.tui.architecture.eventdriven.query.db.repository;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.index.navigable.NavigableIndex;
import com.googlecode.cqengine.query.parser.sql.SQLParser;
import com.googlecode.cqengine.resultset.ResultSet;
import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import com.tui.architecture.eventdriven.query.core.mapper.MemoryEntityMapper;
import com.tui.architecture.eventdriven.query.db.entity.MemoryEntity;
import com.tui.architecture.eventdriven.query.dto.CarCriteria;
import com.tui.architecture.eventdriven.query.dto.OwnerCriteria;
import com.tui.architecture.eventdriven.query.dto.event.CarDTO;
import com.tui.architecture.eventdriven.query.dto.event.OwnerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static com.googlecode.cqengine.codegen.AttributeBytecodeGenerator.createAttributes;
import static com.googlecode.cqengine.query.QueryFactory.equal;

/*
 * Manage memory elements
 *
 * @author joseluis.nogueira on 11/09/2019
 */
@Component
public class MemoryRepository {
  private static final SQLParser<MemoryEntity> SQL_PARSER = SQLParser.forPojoWithAttributes(MemoryEntity.class, createAttributes(MemoryEntity.class));
  private final IndexedCollection<MemoryEntity> memoryEntities;

  @Autowired
  private MemoryEntityMapper memoryEntityMapper;


  public MemoryRepository() {
    memoryEntities = new ConcurrentIndexedCollection<>();
    memoryEntities.addIndex(HashIndex.onAttribute(MemoryEntity.ID));
    memoryEntities.addIndex(HashIndex.onAttribute(MemoryEntity.NAME));
    memoryEntities.addIndex(HashIndex.onAttribute(MemoryEntity.SURNAME));
    memoryEntities.addIndex(NavigableIndex.onAttribute(MemoryEntity.AGE));
    memoryEntities.addIndex(HashIndex.onAttribute(MemoryEntity.REGISTRATION));
    memoryEntities.addIndex(HashIndex.onAttribute(MemoryEntity.BRAND));
    memoryEntities.addIndex(HashIndex.onAttribute(MemoryEntity.MODEL));
    memoryEntities.addIndex(HashIndex.onAttribute(MemoryEntity.COLOR));
    memoryEntities.addIndex(NavigableIndex.onAttribute(MemoryEntity.YEAR));
  }

  public void save(OwnerDTO ownerDTO) {
    ResultSet<MemoryEntity> memoryRepositoryResultSet = findOwnerById(ownerDTO.getId());
    boolean insert = memoryRepositoryResultSet.isEmpty();
    if (insert) {
      memoryEntities.add(memoryEntityMapper.toMemoryEntity(ownerDTO));
    } else {
      Set<MemoryEntity> newObjects = memoryRepositoryResultSet.stream().map(e -> memoryEntityMapper.toMemoryEntity(ownerDTO, memoryEntityMapper.toCarDTO(e))).collect(Collectors.toSet());
      memoryEntities.update(memoryRepositoryResultSet, newObjects);
    }

  }

  public void save(CarDTO carDTO) {
    ResultSet<MemoryEntity> memoryRepositoryResultSet = findCarByRegistration(carDTO.getRegistration());
    boolean insert = memoryRepositoryResultSet.isEmpty();
    ResultSet<MemoryEntity> ownerResultSet = findOwnerById(carDTO.getOwner());
    MemoryEntity firstMemoryEntity = ownerResultSet.stream().findFirst().orElse(null);

    if (insert) {
      OwnerDTO ownerDTO = firstMemoryEntity != null ? memoryEntityMapper.toOwnerDTO(firstMemoryEntity) : createEmptyOwner(carDTO.getOwner());
      MemoryEntity newObject = memoryEntityMapper.toMemoryEntity(ownerDTO, carDTO);
      if (ownerResultSet.size() == 1 && firstMemoryEntity != null && firstMemoryEntity.getRegistration() == null) {
        memoryEntities.update(ownerResultSet, Collections.singleton(newObject));
      } else {
        memoryEntities.add(newObject);
      }
    } else {
      final OwnerDTO ownerDTO;
      if (firstMemoryEntity != null && firstMemoryEntity.getId().equals(carDTO.getOwner())){
        // Change owner
        ownerDTO = memoryEntityMapper.toOwnerDTO(findOwnerById(carDTO.getOwner()).stream().findFirst().get());
      } else {
        ownerDTO = memoryEntityMapper.toOwnerDTO(firstMemoryEntity);
      }

      Set<MemoryEntity> newObjects = memoryRepositoryResultSet.stream().map(e -> memoryEntityMapper.toMemoryEntity(ownerDTO, carDTO)).collect(Collectors.toSet());
      memoryEntities.update(memoryRepositoryResultSet, newObjects);
    }
  }

  public void delete(OwnerDTO ownerDTO) {
    memoryEntities.removeIf(e -> memoryEntityMapper.toOwnerDTO(e).equals(ownerDTO));
  }

  public void delete(CarDTO carDTO) {
    ResultSet<MemoryEntity> resultSet = findOwnerById(carDTO.getOwner());

    OwnerDTO emptyOwner = resultSet.size() == 1 ? memoryEntityMapper.toOwnerDTO(resultSet.stream().findFirst().get()) : null;
    memoryEntities.removeIf(e -> memoryEntityMapper.toCarDTO(e).equals(carDTO));

    if (emptyOwner != null){
      save(emptyOwner);
    }
  }

  public ResultSet<MemoryEntity> findOwnerById(String id) {
    return memoryEntities.retrieve(equal(MemoryEntity.ID, id));
  }

  public ResultSet<MemoryEntity> findOwnerByCriteria(OwnerCriteria ownerCriteria) {
    DbTable orderTable = new DbTable(new DbSpec().addDefaultSchema(), "memoryEntities", "");
    SelectQuery query = new SelectQuery().addAllColumns().addFromTable(orderTable);
    if (ownerCriteria.getId() != null) {
      query.addCondition(BinaryCondition.equalTo("id", ownerCriteria.getId()));
    }
    if (ownerCriteria.getName() != null) {
      query.addCondition(BinaryCondition.equalTo("name", ownerCriteria.getName()));
    }
    if (ownerCriteria.getSurname() != null) {
      query.addCondition(BinaryCondition.equalTo("surname", ownerCriteria.getSurname()));
    }
    if (ownerCriteria.getMinAge() != null) {
      query.addCondition(BinaryCondition.greaterThanOrEq("age", ownerCriteria.getMinAge()));
    }
    if (ownerCriteria.getMaxAge() != null) {
      query.addCondition(BinaryCondition.lessThanOrEq("age", ownerCriteria.getMaxAge()));
    }

    return SQL_PARSER.retrieve(memoryEntities, query.validate().toString());
  }

  public ResultSet<MemoryEntity> findCarByRegistration(String registration) {
    return memoryEntities.retrieve(equal(MemoryEntity.REGISTRATION, registration));
  }

  public ResultSet<MemoryEntity> findCarByCriteria(CarCriteria carCriteria) {
    DbTable orderTable = new DbTable(new DbSpec().addDefaultSchema(), "memoryEntities", "");
    SelectQuery query = new SelectQuery().addAllColumns().addFromTable(orderTable);
    if (carCriteria.getRegistration() != null){
      query.addCondition(BinaryCondition.equalTo("registration", carCriteria.getRegistration()));
    }
    if (carCriteria.getBrand() != null){
      query.addCondition(BinaryCondition.equalTo("brand", carCriteria.getBrand()));
    }
    if (carCriteria.getModel() != null){
      query.addCondition(BinaryCondition.equalTo("model", carCriteria.getModel()));
    }
    if (carCriteria.getColor() != null){
      query.addCondition(BinaryCondition.equalTo("color", carCriteria.getColor()));
    }
    if (carCriteria.getMinYear() != null) {
      query.addCondition(BinaryCondition.greaterThanOrEq("year", carCriteria.getMinYear()));
    }
    if (carCriteria.getMaxYear() != null) {
      query.addCondition(BinaryCondition.lessThanOrEq("year", carCriteria.getMaxYear()));
    }

    return SQL_PARSER.retrieve(memoryEntities, query.validate().toString());
  }

  private OwnerDTO createEmptyOwner(String id){
    OwnerDTO ownerDTO = new OwnerDTO();
    ownerDTO.setId(id);
    ownerDTO.setName("");
    ownerDTO.setSurname("");
    ownerDTO.setAge(0);
    return ownerDTO;
  }
}
