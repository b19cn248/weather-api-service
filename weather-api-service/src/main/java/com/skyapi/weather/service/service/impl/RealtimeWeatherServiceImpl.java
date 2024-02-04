package com.skyapi.weather.service.service.impl;

import com.skyapi.weather.common.dto.request.RealtimeWeatherRequest;
import com.skyapi.weather.common.dto.response.RealTimeWeatherResponse;
import com.skyapi.weather.common.entity.Location;
import com.skyapi.weather.common.entity.RealtimeWeather;
import com.skyapi.weather.service.exception.LocationNotFoundException;
import com.skyapi.weather.service.repository.RealTimeWeatherRepository;
import com.skyapi.weather.service.service.RealtimeWeatherService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class RealtimeWeatherServiceImpl implements RealtimeWeatherService {

  private final RealTimeWeatherRepository repository;

  @Override
  public RealTimeWeatherResponse getByLocation(Location location) {

    log.info("Getting weather for location: {}", location);


    String countryCode = location.getCountryCode();
    String city = location.getCityName();

    RealtimeWeather weather = repository.findByCountryCodeAndCity(countryCode, city);

    if (weather == null) {
      log.info("Weather not found for location: {}", location);
      throw new LocationNotFoundException("Weather not found for location: " + location);
    }

    return entity2DTO(weather);
  }

  @Override
  public RealTimeWeatherResponse getByLocationCode(String locationCode) {
    log.info("Getting weather for location code: {}", locationCode);

    RealtimeWeather weather = repository.findByLocationCode(locationCode);

    if (weather == null) {
      log.info("Weather not found for location code: {}", locationCode);
      throw new LocationNotFoundException("Weather not found for location code: " + locationCode);
    }

    return entity2DTO(weather);
  }

  @Override
  @Transactional
  public RealTimeWeatherResponse update(String locationCode, RealtimeWeatherRequest request) {
    log.info("Updating weather for location code: {}", locationCode);

    RealtimeWeather weather = repository.findByLocationCode(locationCode);

    if (weather == null) {
      log.info("Weather not found for location code: {}", locationCode);
      throw new LocationNotFoundException("Weather not found for location code: " + locationCode);
    }

    RealtimeWeather updatedWeather = dto2Entity(request);

    updatedWeather.setLocation(weather.getLocation());

    RealtimeWeather savedWeather = repository.save(updatedWeather);

    return entity2DTO(savedWeather);
  }

  private RealTimeWeatherResponse entity2DTO(RealtimeWeather weather) {
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

  private RealtimeWeather dto2Entity(RealtimeWeatherRequest request) {
    return RealtimeWeather.builder()
          .temperature(request.getTemperature())
          .humidity(request.getHumidity())
          .precipitation(request.getPrecipitation())
          .windSpeed(request.getWindSpeed())
          .status(request.getStatus())
          .lastUpdated(new Date())
          .build();
  }
}
