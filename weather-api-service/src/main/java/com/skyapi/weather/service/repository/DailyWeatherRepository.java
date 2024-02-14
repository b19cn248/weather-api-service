package com.skyapi.weather.service.repository;

import com.skyapi.weather.common.entity.DailyWeather;
import com.skyapi.weather.common.entity.DailyWeatherId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DailyWeatherRepository extends JpaRepository<DailyWeather, DailyWeatherId> {
  @Query(value = "SELECT d FROM DailyWeather d WHERE d.id.location.code = ?1")
  List<DailyWeather> findByIdLocationCode(String locationCode);
}
