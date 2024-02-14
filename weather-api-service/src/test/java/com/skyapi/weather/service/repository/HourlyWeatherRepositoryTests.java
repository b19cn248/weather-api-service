package com.skyapi.weather.service.repository;

import com.skyapi.weather.common.entity.HourlyWeather;
import com.skyapi.weather.common.entity.HourlyWeatherId;
import com.skyapi.weather.common.entity.Location;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
@TestMethodOrder(org.junit.jupiter.api.MethodOrderer.OrderAnnotation.class)
class HourlyWeatherRepositoryTests {


  @Autowired
  private HourlyWeatherRepository hourlyWeatherRepository;

  @Test
  @Order(1)
  void testAddSuccess() {
    String locationCode = "DELHI_IN";
    int hourOfDay = 12;

    Location location = Location.builder().
          code(locationCode).
          cityName("Delhi").
          regionName("Delhi").
          countryCode("IN").
          countryName("India").
          enabled(true).
          build();

    HourlyWeather hourlyWeather = HourlyWeather.builder()
          .id(new HourlyWeatherId(hourOfDay, location))
          .temperature(30)
          .precipitation(0)
          .status("Sunny")
          .build();

    HourlyWeather updatedHourlyWeather = hourlyWeatherRepository.save(hourlyWeather);

    assertThat(updatedHourlyWeather).isNotNull();
    assertThat(updatedHourlyWeather.getId().getLocation().getCode()).isEqualTo(locationCode);
    assertThat(updatedHourlyWeather.getId().getHourOfDay()).isEqualTo(hourOfDay);
  }

  @Test
  @Order(2)
  void testDeleteSuccess() {
    String locationCode = "DELHI_IN";
    int hourOfDay_12 = 12;

    HourlyWeatherId hourlyWeatherId_12 = new HourlyWeatherId(hourOfDay_12, Location.builder().code(locationCode).build());

    hourlyWeatherRepository.deleteById(hourlyWeatherId_12);

    assertThat(hourlyWeatherRepository.findById(hourlyWeatherId_12)).isEmpty();
  }

  @Test
  @Order(3)
  void testFindByIdLocationCodeSuccess() {
    String locationCode = "DELHI_IN";
    int currentHourOfDay = 9;

    List<HourlyWeather> hourlyWeathers = hourlyWeatherRepository.findByIdLocationCode(locationCode, currentHourOfDay);

    assertThat(hourlyWeathers).isNotNull()
          .hasSize(5);
  }

  @Test
  @Order(4)
  void testFindByIdLocationCodeNotFound() {
    String locationCode = "DELHI_IN";
    int currentHourOfDay = 18;

    List<HourlyWeather> hourlyWeathers = hourlyWeatherRepository.findByIdLocationCode(locationCode, currentHourOfDay);

    assertThat(hourlyWeathers).isNotNull()
          .isEmpty();
  }
}
