package com.skyapi.weather.service.configuration;

import com.skyapi.weather.service.service.MapperService;
import com.skyapi.weather.service.service.impl.MapperServiceImpl;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;

@TestConfiguration
@EnableJpaRepositories(basePackages = {"com.skyapi.weather.service.repository"},
      entityManagerFactoryRef = "entityManagerFactoryTest",
      transactionManagerRef = "transactionManagerTest ")
@ComponentScan(basePackages = {"com.skyapi.weather.service.repository"})
@EntityScan({"com.skyapi.weather.common"})
public class ServiceConfigurationTests {

  @Bean
  public MapperService mapperService() {
    return new MapperServiceImpl();
  }

  @Bean
  public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
    dataSource.setUrl("jdbc:mysql://weatherdb.c3qiemaeiwnu.ap-southeast-2.rds.amazonaws.com:3306/weather_api?allowPublicKeyRetrieval=true&useSSL=false");
    dataSource.setUsername("admin");
    dataSource.setPassword("admin1234");
    dataSource.setSchema("weather_api");
    return dataSource;
  }

  @Bean
  public EntityManagerFactory entityManagerFactoryTest() throws IOException {

    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setGenerateDdl(true);

    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setJpaVendorAdapter(vendorAdapter);
    factory.setPackagesToScan("com.skyapi.weather.common.entity");
    factory.setDataSource(dataSource());
    factory.afterPropertiesSet();
    return factory.getObject();
  }

  @Bean
  public PlatformTransactionManager transactionManagerTest() throws IOException {
    JpaTransactionManager txManager = new JpaTransactionManager();
    txManager.setEntityManagerFactory(entityManagerFactoryTest());
    return txManager;
  }
}
