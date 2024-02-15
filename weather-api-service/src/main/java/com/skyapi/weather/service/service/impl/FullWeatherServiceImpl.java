package com.skyapi.weather.service.service.impl;

import com.skyapi.weather.common.dto.response.FullWeatherResponse;
import com.skyapi.weather.common.dto.response.HourlyWeatherResponse;
import com.skyapi.weather.common.dto.response.ListHourlyWeatherResponse;
import com.skyapi.weather.common.dto.response.RealTimeWeatherResponse;
import com.skyapi.weather.common.entity.HourlyWeather;
import com.skyapi.weather.common.entity.Location;
import com.skyapi.weather.service.exception.LocationNotFoundException;
import com.skyapi.weather.service.repository.LocationRepository;
import com.skyapi.weather.service.service.FullWeatherService;
import com.skyapi.weather.service.service.MapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FullWeatherServiceImpl implements FullWeatherService {

  private final LocationRepository repository;
  private final MapperService mapperService;

  @Override
  public FullWeatherResponse getFullWeather(Location location) {
    log.info("getFullWeather: {}", location);

    Location locationFromDb = repository.findByCountryCodeAndCityName(location.getCountryCode(), location.getCityName());

    if (locationFromDb == null) {
      throw new LocationNotFoundException("Location not found: " + location);
    }

    return convertToResponse(locationFromDb);
  }

  private FullWeatherResponse convertToResponse(Location location) {
    return FullWeatherResponse.builder()
          .location(location.toString())
          .realTimeWeather(
                RealTimeWeatherResponse.builder()
                      .temperature(location.getRealtimeWeather().getTemperature())
                      .humidity(location.getRealtimeWeather().getHumidity())
                      .precipitation(location.getRealtimeWeather().getPrecipitation())
                      .windSpeed(location.getRealtimeWeather().getWindSpeed())
                      .status(location.getRealtimeWeather().getStatus())
                      .lastUpdated(location.getRealtimeWeather().getLastUpdated())
                      .build()
          )
          .hourlyForecast(mapperService.convertToResponse(location.getHourlyWeathers()))
          .dailyForecast(mapperService.convertToResponse(location.getDailyWeathers(), location))
          .build();
  }

  private ListHourlyWeatherResponse convertToResponse(List<HourlyWeather> hourlyWeathers) {


    if (hourlyWeathers.isEmpty()) {
      return new ListHourlyWeatherResponse();
    }

    ListHourlyWeatherResponse response = new ListHourlyWeatherResponse();

    Location location = hourlyWeathers.get(0).getId().getLocation();

    response.setLocation(location.toString());

    hourlyWeathers.forEach(hourlyWeather -> response.addHourlyWeather(
          HourlyWeatherResponse.builder()
                .hourOfDay(hourlyWeather.getId().getHourOfDay())
                .temperature(hourlyWeather.getTemperature())
                .precipitation(hourlyWeather.getPrecipitation())
                .status(hourlyWeather.getStatus())
                .build()
    ));
    return response;
  }

}
