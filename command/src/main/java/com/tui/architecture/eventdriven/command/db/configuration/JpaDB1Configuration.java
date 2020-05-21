package com.tui.architecture.eventdriven.command.db.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/*
 * Jpa Configuration
 *
 * @author joseluis.nogueira on 28/08/2019
 */
@Configuration
@EnableJpaRepositories(
  entityManagerFactoryRef="entityManagerFactory",
  transactionManagerRef="transactionManager",
  basePackages = {"com.tui.architecture.eventdriven.command.db.repository"})
@EnableTransactionManagement
public class JpaDB1Configuration {

  /**
   *
   * @param builder
   * @param dataSource
   * @return
   */
  @Primary
  @Bean(name = "entityManagerFactory")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier("dataSource") DataSource dataSource) {
    return builder
      .dataSource(dataSource)
      .packages("com.tui.architecture.eventdriven.command.db.entity")
      .build();
  }

  /**
   *
   * @param entityManagerFactory
   * @return
   */
  @Primary
  @Bean(name = "transactionManager")
  public PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }

}
