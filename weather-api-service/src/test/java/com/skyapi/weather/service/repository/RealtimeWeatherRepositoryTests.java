package com.skyapi.weather.service.repository;

import com.skyapi.weather.common.entity.RealtimeWeather;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class RealtimeWeatherRepositoryTests {

  @Autowired
  private RealTimeWeatherRepository repository;

  @Test
  void testUpdateSuccess() {

    String locationCode = "NYC_USA";

    RealtimeWeather weather = repository.findById(locationCode).orElse(null);

    weather.setLocationCode("NYC_USA");
    weather.setTemperature(30);
    weather.setHumidity(85);
    weather.setPrecipitation(0);
    weather.setWindSpeed(10);
    weather.setStatus("Sunny");
    weather.setLastUpdated(new Date());

    RealtimeWeather savedWeather = repository.save(weather);

    assertThat(savedWeather).isNotNull();
    assertThat(savedWeather.getLocationCode()).isEqualTo("NYC_USA");
    assertThat(savedWeather.getTemperature()).isEqualTo(30);
    assertThat(savedWeather.getHumidity()).isEqualTo(85);
    assertThat(savedWeather.getPrecipitation()).isZero();
    assertThat(savedWeather.getWindSpeed()).isEqualTo(10);
    assertThat(savedWeather.getStatus()).isEqualTo("Sunny");
  }

  @Test
  void testFindByCountryCodeAndCityNotFound() {
    String countryCode = "JP";
    String city = "Tokyo";

    RealtimeWeather weather = repository.findByCountryCodeAndCity(countryCode, city);

    assertThat(weather).isNull();
  }

  @Test
  void testFindByCountryCodeAndCitySuccess() {
    String countryCode = "US";
    String city = "New York City";

    RealtimeWeather weather = repository.findByCountryCodeAndCity(countryCode, city);

    assertThat(weather).isNotNull();
  }

  @Test
  void testFindByLocationNotFound() {
    String locationCode = "ABC_USA";

    RealtimeWeather weather = repository.findByLocationCode(locationCode);

    assertThat(weather).isNull();
  }

  @Test
  void testFindByLocationIsTrashed() {
    String locationCode = "HN";

    RealtimeWeather weather = repository.findByLocationCode(locationCode);

    assertThat(weather).isNull();
  }

  @Test
  void testFindByLocationSuccess() {
    String locationCode = "NYC_USA";

    RealtimeWeather weather = repository.findByLocationCode(locationCode);

    assertThat(weather).isNotNull();
  }
}
