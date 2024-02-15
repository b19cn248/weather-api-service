package com.skyapi.weather.service.service.impl;

import com.skyapi.weather.common.dto.request.HourlyWeatherRequest;
import com.skyapi.weather.common.dto.response.ListHourlyWeatherResponse;
import com.skyapi.weather.common.entity.HourlyWeather;
import com.skyapi.weather.common.entity.HourlyWeatherId;
import com.skyapi.weather.common.entity.Location;
import com.skyapi.weather.service.exception.LocationNotFoundException;
import com.skyapi.weather.service.repository.HourlyWeatherRepository;
import com.skyapi.weather.service.repository.LocationRepository;
import com.skyapi.weather.service.service.HourlyWeatherService;
import com.skyapi.weather.service.service.MapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HourlyWeatherServiceImpl implements HourlyWeatherService {

  private final HourlyWeatherRepository repository;
  private final LocationRepository locationRepository;
  private final MapperService mapperService;

  @Override
  public ListHourlyWeatherResponse getHourlyWeather(Location location, int currentHourOfDay) {
    log.info("Getting hourly weather for location: {} and current hour of day: {}", location, currentHourOfDay);

    String countryCode = location.getCountryCode();
    String cityName = location.getCityName();

    Location locationInDB = locationRepository.findByCountryCodeAndCityName(countryCode, cityName);

    if (locationInDB == null) {
      log.warn("Location not found for country code: {} and city name: {}", countryCode, cityName);
      throw new LocationNotFoundException("Location not found for country code: " + countryCode + " and city name: " + cityName);
    }

    String locationCode = locationInDB.getCode();
    List<HourlyWeather> hourlyWeathers = repository.findByIdLocationCode(locationCode, currentHourOfDay);

    return ListHourlyWeatherResponse.builder()
          .location(location.toString())
          .hourlyWeather(mapperService.convertToResponse(hourlyWeathers))
          .build();
  }

  @Override
  public ListHourlyWeatherResponse getHourlyWeather(String locationCode, int currentHourOfDay) {
    log.info("Getting hourly weather for location code: {} and current hour of day: {}", locationCode, currentHourOfDay);

    Location location = locationRepository.findByCode(locationCode)
          .orElseThrow(() -> new LocationNotFoundException("Location not found for code: " + locationCode));

    List<HourlyWeather> hourlyWeathers = repository.findByIdLocationCode(location.getCode(), currentHourOfDay);

    return ListHourlyWeatherResponse.builder()
          .location(location.toString())
          .hourlyWeather(mapperService.convertToResponse(hourlyWeathers))
          .build();
  }

  @Override
  public ListHourlyWeatherResponse updateHourlyWeather(String locationCode, List<HourlyWeatherRequest> hourlyWeatherList) {

    Location location = locationRepository.findByCode(locationCode)
          .orElseThrow(() -> new LocationNotFoundException("Location not found for code: " + locationCode));

    log.info("Updating hourly weather for location code: {} and hourlyWeatherList:{}", locationCode, hourlyWeatherList);

    List<HourlyWeather> hourlyWeathersINDB = location.getHourlyWeathers();

    List<HourlyWeather> hourlyWeathersInRequest = convertToEntity(hourlyWeatherList, location);

    List<HourlyWeather> hourlyWeathersToDelete = new ArrayList<>();

    for (HourlyWeather hourlyWeather : hourlyWeathersINDB) {
      if (!hourlyWeathersInRequest.contains(hourlyWeather)) {
        hourlyWeathersToDelete.add(hourlyWeather.getShallowCopy());
      }
    }

    for (HourlyWeather hourlyWeather : hourlyWeathersToDelete) {
      hourlyWeathersINDB.remove(hourlyWeather);
    }

    repository.saveAll(hourlyWeathersInRequest);

    return ListHourlyWeatherResponse.builder()
          .location(location.toString())
          .hourlyWeather(mapperService.convertToResponse(hourlyWeathersInRequest))
          .build();
  }

  private List<HourlyWeather> convertToEntity(List<HourlyWeatherRequest> hourlyWeatherList, Location location) {

    log.debug("Converting hourly weather request to entity: {}", hourlyWeatherList);

    List<HourlyWeather> hourlyWeathers = new ArrayList<>();

    hourlyWeatherList.forEach(hourlyWeatherRequest -> hourlyWeathers.add(
          HourlyWeather.builder()
                .id(new HourlyWeatherId(hourlyWeatherRequest.getHourOfDay(), location))
                .temperature(hourlyWeatherRequest.getTemperature())
                .precipitation(hourlyWeatherRequest.getPrecipitation())
                .status(hourlyWeatherRequest.getStatus())
                .build()
    ));

    return hourlyWeathers;
  }

}
