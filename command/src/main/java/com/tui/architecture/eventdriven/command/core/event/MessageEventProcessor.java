package com.tui.architecture.eventdriven.command.core.event;

import com.tui.architecture.eventdriven.command.core.kafka.DataModifiedEventsProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/*
 * Processor Spring Events
 *
 * @author joseluis.nogueira on 28/08/2019
 */
@Component
@Slf4j
public class MessageEventProcessor {
  private static final long RETRY_TIME_MS = 120000;
  private static final long WAIT_FOR_SEND_TOMBSTONE = 60000;
  private final Executor executor;
  private final Executor deleteExecutor;

  @Autowired
  private DataModifiedEventsProducer dataModifiedEventsProducer;

  public MessageEventProcessor(){
    executor = Executors.newSingleThreadExecutor();
    deleteExecutor = Executors.newCachedThreadPool();
  }

  /**
   * Send message to Kafka
   *
   * @param dataModifiedEvent the event
   */
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void afterEvent(DataModifiedEvent dataModifiedEvent) {
    executor.execute(new Task(dataModifiedEvent));
  }

  class Task implements Runnable{
    private final DataModifiedEvent dataModifiedEvent;

    public Task(DataModifiedEvent dataModifiedEvent){
      this.dataModifiedEvent = dataModifiedEvent;
    }

    @Override
    public void run() {
      boolean send;
      try {
        send = dataModifiedEventsProducer.sendDataModifiedEvent(dataModifiedEvent);
        if (send && dataModifiedEvent.getOperation() == EEventOperation.DELETED && dataModifiedEvent.getSource() != KafkaNull.INSTANCE){
          deleteExecutor.execute(new DeleteTask(dataModifiedEvent.getKey()));
        }
      } catch (Exception e) {
        log.error("[KAFKA] Error produced sending: {} to topic: {}", dataModifiedEvent, e);
        send = false;
      }
      if (!send){
        try {
          Thread.sleep(RETRY_TIME_MS);
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
        }
        run();
      }
    }
  }

  class DeleteTask implements Runnable{
    private final long initialTime;
    private final String key;

    public DeleteTask(String key){
      initialTime = System.currentTimeMillis();
      this.key = key;
    }

    @Override
    public void run() {
      long t = System.currentTimeMillis();
      if (initialTime + WAIT_FOR_SEND_TOMBSTONE > t){
        try {
          Thread.sleep((t + WAIT_FOR_SEND_TOMBSTONE) - initialTime);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          return;
        }
      }
      deleteExecutor.execute(new Task(new DataModifiedEvent(key, null, null, EEventOperation.DELETED)));
    }
  }

}
