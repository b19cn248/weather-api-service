package com.skyapi.weather.service.service;

import com.skyapi.weather.common.dto.request.DailyWeatherRequest;
import com.skyapi.weather.common.dto.response.ListDailyWeatherResponse;
import com.skyapi.weather.common.entity.Location;

import java.util.List;

public interface DailyWeatherService {
  ListDailyWeatherResponse getDailyWeather(Location location);

  ListDailyWeatherResponse getDailyWeather(String locationCode);

  ListDailyWeatherResponse update(String locationCode, List<DailyWeatherRequest> dailyWeatherRequestList);
}
