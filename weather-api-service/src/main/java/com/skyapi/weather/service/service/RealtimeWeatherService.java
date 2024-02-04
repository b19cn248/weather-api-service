package com.skyapi.weather.service.service;

import com.skyapi.weather.common.dto.request.RealtimeWeatherRequest;
import com.skyapi.weather.common.dto.response.RealTimeWeatherResponse;
import com.skyapi.weather.common.entity.Location;
import com.skyapi.weather.common.entity.RealtimeWeather;

public interface RealtimeWeatherService {
  RealTimeWeatherResponse getByLocation(Location location);

  RealTimeWeatherResponse getByLocationCode(String locationCode);

  RealTimeWeatherResponse update(String locationCode, RealtimeWeatherRequest request);
}
