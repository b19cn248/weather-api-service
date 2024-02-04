package com.skyapi.weather.service.controller;

import com.skyapi.weather.common.dto.response.RealTimeWeatherResponse;
import com.skyapi.weather.common.entity.Location;
import com.skyapi.weather.service.exception.GeolocationNotFoundException;
import com.skyapi.weather.service.exception.LocationNotFoundException;
import com.skyapi.weather.service.service.GeolocationService;
import com.skyapi.weather.service.service.RealtimeWeatherService;
import com.skyapi.weather.service.utils.CommonUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/realtime")
@RequiredArgsConstructor
@Slf4j
public class RealtimeWeatherController {

  private final RealtimeWeatherService realtimeWeatherService;
  private final GeolocationService geolocationService;

  @GetMapping
  public ResponseEntity<RealTimeWeatherResponse> getRealtimeWeather(HttpServletRequest request) {

    log.info("Request to get realtime weather: {}", request.getRemoteAddr());

    String ipAddress = CommonUtility.getIPAddress(request);

    log.info("IP Address: {}", ipAddress);

    try {
      Location location = geolocationService.getLocation(ipAddress);
      RealTimeWeatherResponse weather = realtimeWeatherService.getByLocation(location);

      return ResponseEntity.ok(weather);
    } catch (GeolocationNotFoundException e) {
      log.error("Error getting location from IP address", e);
      return ResponseEntity.badRequest().build();
    } catch (LocationNotFoundException e) {
      log.error("Error getting weather for location", e);
      return ResponseEntity.notFound().build();
    }
  }
}
