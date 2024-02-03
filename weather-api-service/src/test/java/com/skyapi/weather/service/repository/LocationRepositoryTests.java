package com.skyapi.weather.service.repository;

import com.skyapi.weather.common.dto.response.LocationResponse;
import com.skyapi.weather.common.entity.Location;
import com.skyapi.weather.common.entity.RealtimeWeather;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
@TestMethodOrder(org.junit.jupiter.api.MethodOrderer.OrderAnnotation.class)
class LocationRepositoryTests {

  @Autowired
  private LocationRepository repository;

  @Test
  @Order(1)
  void testAddSuccess() {
    Location location = new Location();
    location.setCode("NYC_USA");
    location.setCityName("New York City");
    location.setRegionName("New York");
    location.setCountryCode("US");
    location.setCountryName("United State of America");
    location.setEnabled(true);

    Location savedLocation = repository.save(location);

    assertThat(savedLocation).isNotNull();
    assertThat(savedLocation.getCode()).isEqualTo("NYC_USA");
  }

  @Test
  @Order(2)
  void testListSuccess() {
    List<LocationResponse> locations = repository.list();

    assertThat(locations).isNotNull();

    locations.forEach(System.out::println);
  }

  @Test
  @Order(3)
  void testGetNotFound() {
    String code = "ABCD";

    Optional<LocationResponse> location = repository.detail(code);

    assertThat(location).isEmpty();
  }

  @Test
  @Order(4)
  void testGetIsTrashed() {
    String code = "HN";

    Optional<LocationResponse> location = repository.detail(code);

    assertThat(location).isEmpty();
  }

  @Test
  @Order(5)
  void testGetIsFound() {
    String code = "NYC_USA";

    Optional<LocationResponse> location = repository.detail(code);

    assertThat(location).isPresent();
  }

  @Test
  @Order(6)
  void testTrashSuccess() {
    String code = "HN";

    repository.softDelete(code);

    Optional<Location> location = repository.findByCode(code);

    assertThat(location).isEmpty();
  }

  @Test
  @Order(7)
  void testAddRealtimeWeatherSuccess() {
    String locationCode = "DELHI_IN";

    Location location = repository.findByCode(locationCode).orElse(null);

    assert location != null;
    RealtimeWeather realtimeWeather = location.getRealtimeWeather();

    if (realtimeWeather == null) {
      realtimeWeather = new RealtimeWeather();
      realtimeWeather.setLocation(location);
      location.setRealtimeWeather(realtimeWeather);
    }

    realtimeWeather.setTemperature(20);
    realtimeWeather.setHumidity(70);
    realtimeWeather.setPrecipitation(2);
    realtimeWeather.setWindSpeed(10);
    realtimeWeather.setStatus("Clear");
    realtimeWeather.setLastUpdated(new Date());

    Location updatedLocation = repository.save(location);
    assertThat(updatedLocation.getRealtimeWeather().getLocationCode()).isEqualTo(locationCode);
  }

  @Test
  @Order(8)
  void testAddRealtimeWeatherUSSuccess() {
    String locationCode = "NYC_USA";

    Location location = repository.findByCode(locationCode).orElse(null);

    assert location != null;
    RealtimeWeather realtimeWeather = location.getRealtimeWeather();

    if (realtimeWeather == null) {
      realtimeWeather = new RealtimeWeather();
      realtimeWeather.setLocation(location);
      location.setRealtimeWeather(realtimeWeather);
    }

    realtimeWeather.setTemperature(30);
    realtimeWeather.setHumidity(80);
    realtimeWeather.setPrecipitation(2);
    realtimeWeather.setWindSpeed(30);
    realtimeWeather.setStatus("Sunny");
    realtimeWeather.setLastUpdated(new Date());

    Location updatedLocation = repository.save(location);
    assertThat(updatedLocation.getRealtimeWeather().getLocationCode()).isEqualTo(locationCode);
  }
}
