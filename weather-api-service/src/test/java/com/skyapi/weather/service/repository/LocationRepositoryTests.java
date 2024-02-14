package com.skyapi.weather.service.repository;

import com.skyapi.weather.common.dto.response.LocationResponse;
import com.skyapi.weather.common.entity.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
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

  @Autowired
  private DailyWeatherRepository dailyWeatherRepository;

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
  void testAddMumBaiSuccess() {
    Location location = new Location();
    location.setCode("MUMBAI_IN");
    location.setCityName("Mumbai");
    location.setRegionName("Maharashtra");
    location.setCountryCode("IN");
    location.setCountryName("India");
    location.setEnabled(true);

    Location savedLocation = repository.save(location);

    assertThat(savedLocation).isNotNull();
    assertThat(savedLocation.getCode()).isEqualTo("MUMBAI_IN");
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

  @Test
  void testAddHourlyWeatherMumBaiSuccess() {
    Location location = repository.findByCode("MUMBAI_IN").orElseThrow();

    List<HourlyWeather> hourlyWeathers = location.getHourlyWeathers();

    HourlyWeather hourlyWeather1 = HourlyWeather.builder()
          .id(new HourlyWeatherId(8, location))
          .temperature(30)
          .precipitation(0)
          .status("Sunny")
          .build();

    HourlyWeather hourlyWeather2 = HourlyWeather.builder()
          .id(new HourlyWeatherId(9, location))
          .temperature(30)
          .precipitation(0)
          .status("Sunny")
          .build();

    hourlyWeathers.add(hourlyWeather1);
    hourlyWeathers.add(hourlyWeather2);

    location.setHourlyWeathers(hourlyWeathers);

    repository.save(location);

    Assertions.assertThat(location.getHourlyWeathers()).hasSize(2);

  }

  @Test
  void testAddHourlyWeatherDelhiSuccess() {
    Location location = repository.findByCode("DELHI_IN").orElseThrow();

    List<HourlyWeather> hourlyWeathers = location.getHourlyWeathers();

    HourlyWeather hourlyWeather1 = HourlyWeather.builder()
          .id(new HourlyWeatherId(10, location))
          .temperature(30)
          .precipitation(20)
          .status("Sunny")
          .build();

    HourlyWeather hourlyWeather2 = HourlyWeather.builder()
          .id(new HourlyWeatherId(11, location))
          .temperature(30)
          .precipitation(40)
          .status("Cloudy")
          .build();

    hourlyWeathers.add(hourlyWeather1);
    hourlyWeathers.add(hourlyWeather2);

    location.setHourlyWeathers(hourlyWeathers);

    repository.save(location);

    Assertions.assertThat(location.getHourlyWeathers()).hasSize(5);
  }

  @Test
  void testFindByCountryCodeAndCityNameNotfound() {
    String countryCode = "VN";
    String cityName = "Ha Noi City";
    Location location = repository.findByCountryCodeAndCityName(countryCode, cityName);
    assertThat(location).isNull();
  }

  @Test
  void testFindByCountryCodeAndCityNameFound() {
    String countryCode = "US";
    String cityName = "New York City";
    Location location = repository.findByCountryCodeAndCityName(countryCode, cityName);
    assertThat(location).isNotNull();
    assertThat(location.getCode()).isEqualTo("NYC_USA");
    assertThat(location.getCityName()).isEqualTo("New York City");
  }

  @Test
  @Order(9)
  void testAddDailyWeatherDataSuccess() {
    String locationCode = "DELHI_IN";
    Location location = repository.findByCode(locationCode).orElseThrow();

    List<DailyWeather> dailyWeathersINDB = location.getDailyWeathers();

    DailyWeather dailyWeather1 = DailyWeather.builder()
          .id(new DailyWeatherId(10, 2, location))
          .minTemp(30)
          .maxTemp(40)
          .precipitation(10)
          .status("Sunny")
          .build();
    DailyWeather dailyWeather2 = DailyWeather.builder()
          .id(new DailyWeatherId(11, 2, location))
          .minTemp(26)
          .maxTemp(35)
          .precipitation(20)
          .status("Cloudy")
          .build();

    List<DailyWeather> dailyWeathersRequest = new ArrayList<>(List.of(dailyWeather1, dailyWeather2));
    List<DailyWeather> dailyWeathersDeleted = new ArrayList<>();

    for (DailyWeather dailyWeather : dailyWeathersINDB) {
      if (!dailyWeathersRequest.contains(dailyWeather)) {
        dailyWeathersDeleted.add(dailyWeather);
      }
    }


    for (DailyWeather dailyWeather : dailyWeathersDeleted) {
      dailyWeathersINDB.remove(dailyWeather);
    }

    dailyWeatherRepository.saveAll(dailyWeathersRequest);

    Location savedLocation = repository.save(location);

    Assertions.assertThat(savedLocation.getDailyWeathers()).hasSize(2);
  }

}
