package com.skyapi.weather.service.controller;

import com.skyapi.weather.common.dto.response.ListDailyWeatherResponse;
import com.skyapi.weather.common.entity.Location;
import com.skyapi.weather.service.exception.GeolocationNotFoundException;
import com.skyapi.weather.service.exception.LocationNotFoundException;
import com.skyapi.weather.service.service.DailyWeatherService;
import com.skyapi.weather.service.service.GeolocationService;
import com.skyapi.weather.service.utils.CommonUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/daily")
@RequiredArgsConstructor
@Slf4j
public class DailyWeatherController {

  private final DailyWeatherService dailyWeatherService;
  private final GeolocationService geolocationService;

  @GetMapping
  public ResponseEntity<ListDailyWeatherResponse> listDailyForecastByIPAddress(HttpServletRequest request) {

    String ipAddress = CommonUtility.getIPAddress(request);

    log.info("IP Address: {}", ipAddress);

    try {
      Location location = geolocationService.getLocation(ipAddress);

      log.info("Location: {}", location.getCode());

      ListDailyWeatherResponse dailyWeather = dailyWeatherService.getDailyWeather(location);

      if (dailyWeather.getDailyWeather().isEmpty()) {
        return ResponseEntity.noContent().build();
      }

      return ResponseEntity.ok(dailyWeather);

    } catch (LocationNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (GeolocationNotFoundException | NumberFormatException e) {
      log.error(e.getMessage(), e);
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/{locationCode}")
  public ResponseEntity<ListDailyWeatherResponse> listDailyForecastByLocationCode(@PathVariable String locationCode) {

    log.info("Location Code: {}", locationCode);

    try {
      ListDailyWeatherResponse dailyWeather = dailyWeatherService.getDailyWeather(locationCode);

      if (dailyWeather.getDailyWeather().isEmpty()) {
        return ResponseEntity.noContent().build();
      }

      return ResponseEntity.ok(dailyWeather);

    } catch (LocationNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }
}
