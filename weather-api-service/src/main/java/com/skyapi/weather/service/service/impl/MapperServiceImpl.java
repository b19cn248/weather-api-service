package com.skyapi.weather.service.service.impl;

import com.skyapi.weather.common.dto.response.DailyWeatherResponse;
import com.skyapi.weather.common.dto.response.HourlyWeatherResponse;
import com.skyapi.weather.common.dto.response.RealTimeWeatherResponse;
import com.skyapi.weather.common.entity.DailyWeather;
import com.skyapi.weather.common.entity.HourlyWeather;
import com.skyapi.weather.common.entity.Location;
import com.skyapi.weather.common.entity.RealtimeWeather;
import com.skyapi.weather.service.service.MapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class MapperServiceImpl implements MapperService {
  @Override
  public List<HourlyWeatherResponse> convertToResponse(List<HourlyWeather> hourlyWeathers) {

    List<HourlyWeatherResponse> response = new ArrayList<>();

    hourlyWeathers.forEach(hourlyWeather -> response.add(
          HourlyWeatherResponse.builder()
                .hourOfDay(hourlyWeather.getId().getHourOfDay())
                .temperature(hourlyWeather.getTemperature())
                .precipitation(hourlyWeather.getPrecipitation())
                .status(hourlyWeather.getStatus())
                .build()
    ));

    return response;
  }

  @Override
  public List<DailyWeatherResponse> convertToResponse(List<DailyWeather> dailyWeathers, Location location) {
    List<DailyWeatherResponse> response = new ArrayList<>();

    dailyWeathers.forEach(dailyWeather -> response.add(
          DailyWeatherResponse.builder()
                .dayOfMonth(dailyWeather.getId().getDayOfMonth())
                .month(dailyWeather.getId().getMonth())
                .minTemp(dailyWeather.getMinTemp())
                .maxTemp(dailyWeather.getMaxTemp())
                .precipitation(dailyWeather.getPrecipitation())
                .status(dailyWeather.getStatus())
                .build()
    ));

    return response;
  }

  @Override
  public RealTimeWeatherResponse entity2DTO(RealtimeWeather weather) {
    return RealTimeWeatherResponse.builder()
          .location(weather.getLocation().getCityName() + ", " + weather.getLocation().getCountryName())
          .temperature(weather.getTemperature())
          .humidity(weather.getHumidity())
          .precipitation(weather.getPrecipitation())
          .windSpeed(weather.getWindSpeed())
          .status(weather.getStatus())
          .lastUpdated(weather.getLastUpdated())
          .build();
  }
}
