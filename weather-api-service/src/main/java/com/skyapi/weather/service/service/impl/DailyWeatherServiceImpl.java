package com.skyapi.weather.service.service.impl;

import com.skyapi.weather.common.dto.request.DailyWeatherRequest;
import com.skyapi.weather.common.dto.response.ListDailyWeatherResponse;
import com.skyapi.weather.common.entity.DailyWeather;
import com.skyapi.weather.common.entity.DailyWeatherId;
import com.skyapi.weather.common.entity.Location;
import com.skyapi.weather.service.exception.LocationNotFoundException;
import com.skyapi.weather.service.repository.DailyWeatherRepository;
import com.skyapi.weather.service.repository.LocationRepository;
import com.skyapi.weather.service.service.DailyWeatherService;
import com.skyapi.weather.service.service.MapperService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DailyWeatherServiceImpl implements DailyWeatherService {

  private final LocationRepository locationRepository;

  private final DailyWeatherRepository repository;

  private final MapperService mapperService;

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
    List<DailyWeather> dailyWeathers = repository.findByIdLocationCode(locationCode);

    return ListDailyWeatherResponse.builder()
          .location(location.toString())
          .dailyWeather(mapperService.convertToResponse(dailyWeathers, location))
          .build();
  }

  @Override
  public ListDailyWeatherResponse getDailyWeather(String locationCode) {
    log.info("getDailyWeather: {}", locationCode);

    Location location = locationRepository.findByCode(locationCode)
          .orElseThrow(() -> new LocationNotFoundException("Location not found for code: " + locationCode));

    List<DailyWeather> dailyWeathers = repository.findByIdLocationCode(location.getCode());

    return ListDailyWeatherResponse.builder()
          .location(location.toString())
          .dailyWeather(mapperService.convertToResponse(dailyWeathers, location))
          .build();
  }

  @Override
  @Transactional
  public ListDailyWeatherResponse update(String locationCode, List<DailyWeatherRequest> dailyWeatherRequestList) {
    log.info("update: {}", locationCode);

    Location location = locationRepository.findByCode(locationCode)
          .orElseThrow(() -> new LocationNotFoundException("Location not found for code: " + locationCode));

    List<DailyWeather> dailyWeathersINDB = location.getDailyWeathers();

    List<DailyWeather> dailyWeathersInRequest = convertToEntity(dailyWeatherRequestList, location);

    List<DailyWeather> dailyWeathersToDelete = new ArrayList<>();

    for (DailyWeather dailyWeather : dailyWeathersINDB) {
      if (!dailyWeathersInRequest.contains(dailyWeather)) {
        dailyWeathersToDelete.add(dailyWeather.copy());
      }
    }

    for (DailyWeather dailyWeather : dailyWeathersToDelete) {
      dailyWeathersINDB.remove(dailyWeather);
    }

    repository.saveAll(dailyWeathersInRequest);

    return ListDailyWeatherResponse.builder()
          .location(location.toString())
          .dailyWeather(mapperService.convertToResponse(dailyWeathersInRequest, location))
          .build();
  }

  private List<DailyWeather> convertToEntity(List<DailyWeatherRequest> dailyWeatherRequestList, Location location) {
    List<DailyWeather> dailyWeathers = new ArrayList<>();

    for (DailyWeatherRequest dailyWeatherRequest : dailyWeatherRequestList) {
      dailyWeathers.add(DailyWeather.builder()
            .id(DailyWeatherId.builder()
                  .dayOfMonth(dailyWeatherRequest.getDayOfMonth())
                  .month(dailyWeatherRequest.getMonth())
                  .location(location)
                  .build())
            .minTemp(dailyWeatherRequest.getMinTemp())
            .maxTemp(dailyWeatherRequest.getMaxTemp())
            .precipitation(dailyWeatherRequest.getPrecipitation())
            .status(dailyWeatherRequest.getStatus())
            .build());
    }

    return dailyWeathers;
  }
}
