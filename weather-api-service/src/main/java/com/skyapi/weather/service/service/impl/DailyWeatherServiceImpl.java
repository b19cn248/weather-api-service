package com.skyapi.weather.service.service.impl;

import com.skyapi.weather.common.dto.response.DailyWeatherResponse;
import com.skyapi.weather.common.dto.response.ListDailyWeatherResponse;
import com.skyapi.weather.common.entity.DailyWeather;
import com.skyapi.weather.common.entity.Location;
import com.skyapi.weather.service.exception.LocationNotFoundException;
import com.skyapi.weather.service.repository.DailyWeatherRepository;
import com.skyapi.weather.service.repository.LocationRepository;
import com.skyapi.weather.service.service.DailyWeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DailyWeatherServiceImpl implements DailyWeatherService {

  private final LocationRepository locationRepository;

  private final DailyWeatherRepository repository;

  @Override
  public ListDailyWeatherResponse getDailyWeather(Location location) {
    log.info("getDailyWeather: {}", location);

    String countryCode = location.getCountryCode();
    String cityName = location.getCityName();

    Location locationInDB = locationRepository.findByCountryCodeAndCityName(countryCode, cityName);

    if (locationInDB == null) {
      log.warn("Location not found for country code: {} and city name: {}", countryCode, cityName);
      throw new LocationNotFoundException("Location not found for country code: " + countryCode + " and city name: " + cityName);
    }

    String locationCode = locationInDB.getCode();
    List<DailyWeather> hourlyWeathers = repository.findByIdLocationCode(locationCode);

    return convertToResponse(hourlyWeathers, location);
  }

  @Override
  public ListDailyWeatherResponse getDailyWeather(String locationCode) {
    log.info("getDailyWeather: {}", locationCode);

    Location location = locationRepository.findByCode(locationCode)
          .orElseThrow(() -> new LocationNotFoundException("Location not found for code: " + locationCode));

    List<DailyWeather> dailyWeathers = repository.findByIdLocationCode(location.getCode());

    return convertToResponse(dailyWeathers, location);
  }

  private ListDailyWeatherResponse convertToResponse(List<DailyWeather> dailyWeathers, Location location) {

    if (dailyWeathers == null || dailyWeathers.isEmpty()) {
      return new ListDailyWeatherResponse();
    }

    ListDailyWeatherResponse response = new ListDailyWeatherResponse();

    response.setLocation(location.toString());

    for (DailyWeather dailyWeather : dailyWeathers) {
      response.addDailyWeather(DailyWeatherResponse.builder()
            .dayOfMonth(dailyWeather.getId().getDayOfMonth())
            .month(dailyWeather.getId().getMonth())
            .minTemp(dailyWeather.getMinTemp())
            .maxTemp(dailyWeather.getMaxTemp())
            .precipitation(dailyWeather.getPrecipitation())
            .status(dailyWeather.getStatus())
            .build());
    }

    return response;
  }
}
