package com.skyapi.weather.service.repository;

import com.skyapi.weather.common.entity.RealtimeWeather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RealTimeWeatherRepository extends JpaRepository<RealtimeWeather, String> {

  @Query("SELECT r FROM RealtimeWeather r WHERE r.location.countryCode = ?1 AND r.location.cityName = ?2")
  RealtimeWeather findByCountryCodeAndCity(String countryCode, String city);

  @Query(value = "SELECT r FROM RealtimeWeather r WHERE r.location.code = ?1 AND r.location.trashed = false")
  RealtimeWeather findByLocationCode(String locationCode);
}
