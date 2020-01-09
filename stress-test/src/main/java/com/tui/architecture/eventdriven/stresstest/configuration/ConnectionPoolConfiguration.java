package com.tui.architecture.eventdriven.stresstest.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * Pool for external connections
 *
 * @author joseluis.nogueira on 17/10/2019
 */
@Configuration
public class ConnectionPoolConfiguration {
  @Bean
  public ExecutorService connectionPool(){
    return Executors.newFixedThreadPool(200);
  }
}
