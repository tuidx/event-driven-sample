package com.tui.architecture.eventdriven.command.core.kafka.configuration;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.PartitionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/*
 * Create topics
 *
 * @author joseluis.nogueira on 09/10/2019
 */
@Configuration
public class CreateTopics {
  private static final String TOPIC_NAME = "synchronization.data-modified-events";

  @Autowired
  private ConsumerFactory<String, String> consumerFactory;

  @Autowired
  private KafkaAdmin kafkaAdmin;

  @PostConstruct
  public void setUp(){
    if (!getTopics().contains(TOPIC_NAME)){
      Properties props = new Properties();
      props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, ((List<String>)consumerFactory.getConfigurationProperties().get(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG)).get(0));

      Map<String, String> configs = new HashMap<>();
      configs.put("cleanup.policy", "compact");
      configs.put("delete.retention.ms", "0"); // 100 - Set to 0 for delete Tombstone (payload = null)
      configs.put("segment.ms", "100");
      configs.put("min.cleanable.dirty.ratio", "0"); // 0.01 - Set to 0 for no keep any old message
      configs.put("flush.ms", "1000");
      NewTopic newTopic = new NewTopic("synchronization.data-modified-events", 1, (short) 1);
      newTopic.configs(configs);

      try(AdminClient adminClient = AdminClient.create(props)){
        adminClient.createTopics(Collections.singleton(newTopic));
      }
    }
  }

  private Set<String> getTopics() {
    try (Consumer<String, String> consumer =
                 consumerFactory.createConsumer()) {
      Map<String, List<PartitionInfo>> map = consumer.listTopics();
      return map.keySet();
    }
  }
}
