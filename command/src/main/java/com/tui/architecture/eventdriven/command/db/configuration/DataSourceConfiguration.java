package com.tui.architecture.eventdriven.command.db.configuration;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.annotation.Validated;

/*
 * DataSource configuration
 *
 * @author joseluis.nogueira on 28/08/2019
 */
@Log4j2
@Configuration
public class DataSourceConfiguration {
  @Primary
  @Bean(name = "dataSource", destroyMethod = "")
  @Validated
  @ConfigurationProperties(prefix = "spring.datasource.postgres")
  @ConditionalOnProperty(
    name = {"spring.datasource.postgres.type"},
    havingValue = "com.zaxxer.hikari.HikariDataSource",
    matchIfMissing = true
  )
  @ConditionalOnClass({HikariDataSource.class})
  public HikariDataSource dataSource() {
    return new HikariDataSource();
  }
}
