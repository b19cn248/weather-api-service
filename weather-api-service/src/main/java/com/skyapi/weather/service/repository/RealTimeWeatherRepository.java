package com.skyapi.weather.service.repository;

import com.skyapi.weather.common.entity.RealtimeWeather;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RealTimeWeatherRepository extends JpaRepository<RealtimeWeather, String> {
}
