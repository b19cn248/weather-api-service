package com.skyapi.weather.service.service;

import com.skyapi.weather.common.dto.request.HourlyWeatherRequest;
import com.skyapi.weather.common.dto.response.ListHourlyWeatherResponse;
import com.skyapi.weather.common.entity.HourlyWeather;
import com.skyapi.weather.common.entity.Location;

import java.util.List;

public interface HourlyWeatherService {
  ListHourlyWeatherResponse getHourlyWeather(Location location, int currentHourOfDay);

  ListHourlyWeatherResponse getHourlyWeather(String locationCode, int currentHourOfDay);

  ListHourlyWeatherResponse updateHourlyWeather(String locationCode, List<HourlyWeatherRequest> hourlyWeatherList);
}
