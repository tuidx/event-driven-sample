package com.tui.architecture.eventdriven.stresstest.core.service;

import com.tui.architecture.eventdriven.stresstest.core.event.DataModifiedEvent;
import com.tui.architecture.eventdriven.stresstest.core.event.EEventOperation;
import com.tui.architecture.eventdriven.stresstest.core.feign.QueryFeign;
import com.tui.architecture.eventdriven.stresstest.core.kafka.DataModifiedEventsProducer;
import com.tui.architecture.eventdriven.stresstest.db.repository.CarRepository;
import com.tui.architecture.eventdriven.stresstest.db.repository.OwnerRepository;
import com.tui.architecture.eventdriven.stresstest.dto.CarDTO;
import com.tui.architecture.eventdriven.stresstest.dto.OwnerDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/*
 * Clean all data
 *
 * @author joseluis.nogueira on 11/10/2019
 */
@Service
@Slf4j
public class CleanService {
  @Autowired
  private OwnerRepository ownerRepository;
  @Autowired
  private CarRepository carRepository;
  @Autowired
  private QueryFeign queryFeign;
  @Autowired
  private DataModifiedEventsProducer dataModifiedEventsProducer;
  @Autowired
  private ExecutorService connectionPool;

  @Autowired
  private ConsumerFactory<byte[], String> consumerFactory;
  @Autowired
  private KafkaTemplate kafkaTemplate;


  public void run(){
    log.info("Delete Database");
    carRepository.deleteAll();
    ownerRepository.deleteAll();

    log.info("Delete queries services");
    deleteOwners();
    deleteCars();

    log.info("Purge Kafka topic");
    purgeKafkaTopic();

    log.info("Clean complete");
  }

  private void deleteOwners(){
    ResponseEntity<List<OwnerDTO>> responseOnwers = queryFeign.retrieveOwnerByCriteria();
    if (responseOnwers.getStatusCode() == HttpStatus.OK){
      for (OwnerDTO ownerDTO : responseOnwers.getBody()){
        boolean send = dataModifiedEventsProducer.sendDataModifiedEvent(new DataModifiedEvent("owner-" + ownerDTO.getId(), OwnerDTO.MEDIA_TYPE, ownerDTO, EEventOperation.DELETED));
        if (!send || !dataModifiedEventsProducer.sendDataModifiedEvent(new DataModifiedEvent("owner-" + ownerDTO.getId(), OwnerDTO.MEDIA_TYPE, null, EEventOperation.DELETED))){
          log.error("Error sending event for delete: " + ownerDTO);
        }
      }
    } else {
      log.error("Error recovering owners");
    }
  }

  private void deleteCars(){
    ResponseEntity<List<CarDTO>> responseCars = queryFeign.retrieveCarByCriteria();
    if (responseCars.getStatusCode() == HttpStatus.OK){
      for(CarDTO carDTO : responseCars.getBody()){
        boolean send = dataModifiedEventsProducer.sendDataModifiedEvent(new DataModifiedEvent("car-" + carDTO.getRegistration(), CarDTO.MEDIA_TYPE, null, EEventOperation.DELETED));
        if (!send || !dataModifiedEventsProducer.sendDataModifiedEvent(new DataModifiedEvent("car-" + carDTO.getRegistration(), CarDTO.MEDIA_TYPE, null, EEventOperation.DELETED))){
          log.error("Error sending event for delete: " + carDTO);
        }
      }
    } else {
      log.error("Error recovering cars");
    }
  }

  private void purgeKafkaTopic(){
    try(Consumer<byte[], String> consumer = consumerFactory.createConsumer()) {
      PartitionInfo partitionInfo = consumer.listTopics().get("synchronization.data-modified-events").stream().findFirst().orElse(null);
      if (partitionInfo != null) {
        Collection<TopicPartition> topic = Collections.singleton(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()));
        consumer.assign(topic);
        consumer.seekToBeginning(topic);

        ConsumerRecords<byte[], String> consumerRecords;
        List<Future<Void>> futures = new ArrayList<>();
        do {
          consumerRecords = consumer.poll(Duration.of(1, ChronoUnit.SECONDS));
          consumerRecords.forEach(e -> {
            if (e.value() != null) {
              futures.add(connectionPool.submit(() -> {
                dataModifiedEventsProducer.sendDataModifiedEvent(new DataModifiedEvent(new String(e.key()), null, null, EEventOperation.DELETED));
                return null;
              }));
            }
          });
        } while (!consumerRecords.isEmpty());
        waitFutures(futures);
      }
    }
  }

  private void waitFutures(List<Future<Void>> futures){
    futures.forEach(e -> {
      try {
        e.get();
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
      } catch (ExecutionException ex) {
        log.error("Error executing operation", e);
      }
    });
  }
}
