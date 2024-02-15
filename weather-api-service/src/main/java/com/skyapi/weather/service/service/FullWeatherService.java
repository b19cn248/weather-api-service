package com.skyapi.weather.service.service;

import com.skyapi.weather.common.dto.response.FullWeatherResponse;
import com.skyapi.weather.common.entity.Location;

public interface FullWeatherService {
  FullWeatherResponse getFullWeather(Location location);
}
