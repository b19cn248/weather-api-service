package com.skyapi.weather.service.service;

import com.skyapi.weather.common.dto.request.DailyWeatherRequest;
import com.skyapi.weather.common.dto.request.HourlyWeatherRequest;
import com.skyapi.weather.common.dto.response.DailyWeatherResponse;
import com.skyapi.weather.common.dto.response.HourlyWeatherResponse;
import com.skyapi.weather.common.dto.response.RealTimeWeatherResponse;
import com.skyapi.weather.common.entity.DailyWeather;
import com.skyapi.weather.common.entity.HourlyWeather;
import com.skyapi.weather.common.entity.Location;
import com.skyapi.weather.common.entity.RealtimeWeather;

import java.util.List;

public interface MapperService {

  List<HourlyWeatherResponse> convertToResponse(List<HourlyWeather> hourlyWeathers);

  List<DailyWeatherResponse> convertToResponse(List<DailyWeather> dailyWeathers, Location location);

  RealTimeWeatherResponse entity2DTO(RealtimeWeather weather);

}
