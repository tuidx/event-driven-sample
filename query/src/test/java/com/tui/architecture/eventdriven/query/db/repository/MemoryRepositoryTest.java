package com.tui.architecture.eventdriven.query.db.repository;

import com.tui.architecture.eventdriven.query.core.mapper.MemoryEntityMapper;
import com.tui.architecture.eventdriven.query.core.mapper.MemoryEntityMapperImpl;
import com.tui.architecture.eventdriven.query.db.entity.MemoryEntity;
import com.tui.architecture.eventdriven.query.dto.CarCriteria;
import com.tui.architecture.eventdriven.query.dto.OwnerCriteria;
import com.tui.architecture.eventdriven.query.dto.event.CarDTO;
import com.tui.architecture.eventdriven.query.dto.event.OwnerDTO;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;

/*
 *
 *
 * @author joseluis.nogueira on 12/09/2019
 */
@RunWith(JMockit.class)
public class MemoryRepositoryTest {
  @Tested
  private MemoryRepository memoryRepository;

  @Injectable
  private MemoryEntityMapper memoryEntityMapper;

  @Before
  public void setUp() {
    memoryEntityMapper = new MemoryEntityMapperImpl();
  }

  @Test
  public void saveOwner() {
    OwnerDTO ownerDTO1 = createOwner("Test Id 1", "Test Name 1", "Test Surname", 18);
    memoryRepository.save(ownerDTO1);

    OwnerDTO ownerDTO2 = createOwner("Test Id 2", "Test Name 2", "Test Surname", 20);
    memoryRepository.save(ownerDTO2);

    OwnerCriteria ownerCriteria;

    assertTrue(memoryRepository.findOwnerById(ownerDTO1.getId()).size() == 1);
    assertTrue(memoryRepository.findOwnerById(ownerDTO2.getId()).size() == 1);

    ownerCriteria = new OwnerCriteria();
    ownerCriteria.setSurname("Test Surname");
    assertTrue(memoryRepository.findOwnerByCriteria(ownerCriteria).size() == 2);

    ownerCriteria = new OwnerCriteria();
    ownerCriteria.setMaxAge(20);
    assertTrue(memoryRepository.findOwnerByCriteria(ownerCriteria).size() == 2);

    ownerCriteria = new OwnerCriteria();
    ownerCriteria.setMaxAge(19);
    List<MemoryEntity> memoryEntities = memoryRepository.findOwnerByCriteria(ownerCriteria).stream().collect(Collectors.toList());
    assertTrue(memoryEntities.size() == 1 && memoryEntities.get(0).getName().equals(ownerDTO1.getName()) && memoryEntities.get(0).getRegistration() == null);

    ownerCriteria = new OwnerCriteria();
    ownerCriteria.setMinAge(19);
    memoryEntities = memoryRepository.findOwnerByCriteria(ownerCriteria).stream().collect(Collectors.toList());
    assertTrue(memoryEntities.size() == 1 && memoryEntities.get(0).getName().equals(ownerDTO2.getName()) && memoryEntities.get(0).getRegistration() == null);

    ownerCriteria = new OwnerCriteria();
    ownerCriteria.setName("Modified name");
    assertTrue(memoryRepository.findOwnerByCriteria(ownerCriteria).size() == 0);
    ownerDTO2.setName("Modified name");
    memoryRepository.save(ownerDTO2);
    assertTrue(memoryRepository.findOwnerByCriteria(ownerCriteria).size() == 1);
  }

  @Test
  public void saveAndDeleteCar() {
    // Add owners
    OwnerDTO ownerDTO1 = createOwner("Test Id 1", "Test Name 1", "Test Surname", 18);
    memoryRepository.save(ownerDTO1);
    OwnerDTO ownerDTO2 = createOwner("Test Id 2", "Test Name 2", "Test Surname", 20);
    memoryRepository.save(ownerDTO2);

    // All cars are null ?
    List<MemoryEntity> memoryEntities;

    memoryEntities = memoryRepository.findOwnerByCriteria(new OwnerCriteria()).stream().collect(Collectors.toList());
    assertTrue(memoryEntities.size() == 2 && memoryEntities.stream().allMatch(e -> e.getRegistration() == null));

    // Add cars
    CarDTO carDTO1 = createCar("Car 1", ownerDTO1.getId(), "Brand 1", "Model 1", "Color 1", 2000);
    memoryRepository.save(carDTO1);
    CarDTO carDTO2 = createCar("Car 2", ownerDTO1.getId(), "Brand 2", "Model 2", "Color 2", 2010);
    memoryRepository.save(carDTO2);
    CarDTO carDTO3 = createCar("Car 3", ownerDTO2.getId(), "Brand 3", "Model 3", "Color 3", 2015);
    memoryRepository.save(carDTO3);

    // All cars are assigned ?
    memoryEntities = memoryRepository.findOwnerByCriteria(new OwnerCriteria()).stream().collect(Collectors.toList());
    assertTrue(memoryEntities.size() == 3 && memoryEntities.stream().allMatch(e -> e.getRegistration() != null));

    // find By Registration
    assertTrue(memoryRepository.findCarByRegistration(carDTO2.getRegistration()).size() == 1);

    // find car By Criterias
    CarCriteria carCriteria;

    carCriteria = new CarCriteria();
    carCriteria.setBrand(carDTO1.getBrand());
    assertTrue(memoryRepository.findCarByCriteria(carCriteria).size() == 1);

    // Modify brand of car
    carDTO2.setBrand(carDTO1.getBrand());
    memoryRepository.save(carDTO2);
    assertTrue(memoryRepository.findCarByCriteria(carCriteria).size() == 2);

    // Modify owner of car
    assertTrue(memoryRepository.findOwnerById(ownerDTO1.getId()).size() == 2);
    assertTrue(memoryRepository.findOwnerById(ownerDTO2.getId()).size() == 1);

    carDTO2.setOwner(ownerDTO2.getId());
    memoryRepository.save(carDTO2);

    assertTrue(memoryRepository.findOwnerById(ownerDTO1.getId()).size() == 1);
    assertTrue(memoryRepository.findOwnerById(ownerDTO2.getId()).size() == 2);

    // Delete owner with two cars
    memoryRepository.delete(ownerDTO2);
    assertTrue(memoryRepository.findOwnerById(ownerDTO2.getId()).size() == 0);

    // Delete car
    memoryRepository.delete(carDTO1);
    memoryEntities = memoryRepository.findOwnerById(ownerDTO1.getId()).stream().collect(Collectors.toList());
    assertTrue(memoryEntities.size() == 1 && memoryEntities.get(0).getId() != null && memoryEntities.get(0).getRegistration() == null);

    // Delete owner
    memoryRepository.delete(ownerDTO1);
    assertTrue(memoryRepository.findOwnerByCriteria(new OwnerCriteria()).size() == 0);
  }

  private OwnerDTO createOwner(String id, String name, String surname, Integer age) {
    OwnerDTO ownerDTO = new OwnerDTO();
    ownerDTO.setId(id);
    ownerDTO.setName(name);
    ownerDTO.setSurname(surname);
    ownerDTO.setAge(age);
    return ownerDTO;
  }

  private CarDTO createCar(String registration, String owner, String brand, String model, String color, Integer year) {
    CarDTO carDTO = new CarDTO();
    carDTO.setRegistration(registration);
    carDTO.setOwner(owner);
    carDTO.setBrand(brand);
    carDTO.setModel(model);
    carDTO.setColor(color);
    carDTO.setYear(year);
    return carDTO;
  }
}