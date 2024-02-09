package com.skyapi.weather.service.controller;

import com.skyapi.weather.common.dto.request.RealtimeWeatherRequest;
import com.skyapi.weather.common.dto.response.RealTimeWeatherResponse;
import com.skyapi.weather.common.entity.Location;
import com.skyapi.weather.service.exception.GeolocationNotFoundException;
import com.skyapi.weather.service.exception.LocationNotFoundException;
import com.skyapi.weather.service.service.GeolocationService;
import com.skyapi.weather.service.service.RealtimeWeatherService;
import com.skyapi.weather.service.utils.CommonUtility;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/realtime")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "REALTIME_WEATHER")
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

  @GetMapping("/{locationCode}")
  public ResponseEntity<RealTimeWeatherResponse> getRealtimeWeatherByLocationCode(@PathVariable String locationCode) {
    try {
      RealTimeWeatherResponse weather = realtimeWeatherService.getByLocationCode(locationCode);
      return ResponseEntity.ok(weather);
    } catch (LocationNotFoundException e) {
      log.error("Error getting weather for location code: {}", locationCode, e);
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("/{locationCode}")
  public ResponseEntity<RealTimeWeatherResponse> updateRealtimeWeather(
        @PathVariable String locationCode,
        @RequestBody @Valid RealtimeWeatherRequest request
  ) {
    try {
      RealTimeWeatherResponse weather = realtimeWeatherService.update(locationCode, request);
      return ResponseEntity.ok(weather);
    } catch (LocationNotFoundException e) {
      log.error("Error updating weather for location code: {}", locationCode, e);
      return ResponseEntity.notFound().build();
    }
  }
}
