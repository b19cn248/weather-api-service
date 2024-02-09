package com.skyapi.weather.service.repository;

import com.skyapi.weather.common.entity.HourlyWeather;
import com.skyapi.weather.common.entity.HourlyWeatherId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HourlyWeatherRepository extends JpaRepository<HourlyWeather, HourlyWeatherId> {

  @Query(value = "SELECT h FROM HourlyWeather h WHERE h.id.location.code = ?1 AND h.id.hourOfDay > ?2" +
        " AND h.id.location.trashed = false")
  List<HourlyWeather> findByIdLocationCode(String locationCode, int currentHourOfDay);
}
