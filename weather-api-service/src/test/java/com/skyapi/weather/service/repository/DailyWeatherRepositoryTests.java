package com.skyapi.weather.service.repository;

import com.skyapi.weather.common.entity.DailyWeather;
import com.skyapi.weather.common.entity.DailyWeatherId;
import com.skyapi.weather.common.entity.Location;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
@TestMethodOrder(org.junit.jupiter.api.MethodOrderer.OrderAnnotation.class)
class DailyWeatherRepositoryTests {

  @Autowired
  private DailyWeatherRepository dailyWeatherRepository;

  @Test
  @Order(1)
  void testAddSuccess() {
    String locationCode = "DELHI_IN";

    Location location = Location.builder().
          code(locationCode).
          cityName("Delhi").
          regionName("Delhi").
          countryCode("IN").
          countryName("India").
          enabled(true).
          build();

    DailyWeather dailyWeather = DailyWeather.builder()
          .id(new DailyWeatherId(12, 2, location))
          .minTemp(30)
          .maxTemp(40)
          .precipitation(0)
          .status("Sunny")
          .build();

    DailyWeather updatedDailyWeather = dailyWeatherRepository.save(dailyWeather);

    assertThat(updatedDailyWeather).isNotNull();
    assertThat(updatedDailyWeather.getId().getLocation().getCode()).isEqualTo(locationCode);
    assertThat(updatedDailyWeather.getId().getDayOfMonth()).isEqualTo(12);
    assertThat(updatedDailyWeather.getId().getMonth()).isEqualTo(2);
  }

  @Test
  @Order(2)
  void testDeleteSuccess() {
    String locationCode = "DELHI_IN";
    int dayOfMonth = 12;
    int month = 2;

    DailyWeatherId dailyWeatherId = new DailyWeatherId(dayOfMonth, month, Location.builder().code(locationCode).build());

    dailyWeatherRepository.deleteById(dailyWeatherId);

    assertThat(dailyWeatherRepository.findById(dailyWeatherId)).isEmpty();
  }
}
