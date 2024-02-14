package com.skyapi.weather.service.service;

import com.skyapi.weather.common.dto.response.ListDailyWeatherResponse;
import com.skyapi.weather.common.entity.Location;

public interface DailyWeatherService {
  ListDailyWeatherResponse getDailyWeather(Location location);

  ListDailyWeatherResponse getDailyWeather(String locationCode);
}
