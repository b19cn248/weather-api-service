package com.skyapi.weather.service.controller;

import com.skyapi.weather.common.dto.response.FullWeatherResponse;
import com.skyapi.weather.common.entity.Location;
import com.skyapi.weather.service.service.FullWeatherService;
import com.skyapi.weather.service.service.GeolocationService;
import com.skyapi.weather.service.utils.CommonUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/full")
@Slf4j
public class FullWeatherController {

  private final FullWeatherService fullWeatherService;
  private final GeolocationService geolocationService;

  @GetMapping
  public ResponseEntity<?> getFullWeather(HttpServletRequest request) {
    log.info("getFullWeather");

    String ipAddress = CommonUtility.getIPAddress(request);


    Location location = geolocationService.getLocation(ipAddress);
    FullWeatherResponse response = fullWeatherService.getFullWeather(location);

    return ResponseEntity.ok(response);

  }
}
