package com.skyapi.weather.service.controller;

import com.skyapi.weather.common.dto.request.HourlyWeatherRequest;
import com.skyapi.weather.common.dto.response.ListHourlyWeatherResponse;
import com.skyapi.weather.common.entity.Location;
import com.skyapi.weather.service.exception.base.BadRequestException;
import com.skyapi.weather.service.exception.GeolocationNotFoundException;
import com.skyapi.weather.service.exception.LocationNotFoundException;
import com.skyapi.weather.service.service.GeolocationService;
import com.skyapi.weather.service.service.HourlyWeatherService;
import com.skyapi.weather.service.utils.CommonUtility;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.skyapi.weather.service.constant.CommonConstant.Constants.HOUR_HEADING;

@RestController
@RequestMapping("/api/v1/hourly")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "HOURLY_WEATHER")
public class HourlyWeatherController {

  private final HourlyWeatherService hourlyWeatherService;
  private final GeolocationService geolocationService;


  @GetMapping
  public ResponseEntity<ListHourlyWeatherResponse> listHourlyForecastByIPAddress(
        HttpServletRequest request,
        @RequestHeader(name = HOUR_HEADING) String currentHour
  ) {
    String ipAddress = CommonUtility.getIPAddress(request);

    log.info("IP Address: {}", ipAddress);

    try {
      int currentHourOfDay = Integer.parseInt(currentHour);
      Location location = geolocationService.getLocation(ipAddress);

      log.info("Location: {}", location.getCode());

      ListHourlyWeatherResponse hourlyWeather = hourlyWeatherService.getHourlyWeather(location, currentHourOfDay);

      if (hourlyWeather.getHourlyWeather().isEmpty()) {
        return ResponseEntity.noContent().build();
      }

      return ResponseEntity.ok(hourlyWeather);

    } catch (LocationNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (GeolocationNotFoundException | NumberFormatException e) {
      log.error(e.getMessage(), e);
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/{locationCode}")
  public ResponseEntity<ListHourlyWeatherResponse> listHourlyForecastByLocationCode(
        @PathVariable String locationCode,
        @RequestHeader(name = HOUR_HEADING) String currentHour
  ) {
    log.info("Location code: {}", locationCode);

    try {
      int currentHourOfDay = Integer.parseInt(currentHour);

      ListHourlyWeatherResponse hourlyWeather = hourlyWeatherService.getHourlyWeather(locationCode, currentHourOfDay);

      log.info("Hourly weather: {}", hourlyWeather.getHourlyWeather());

      if (hourlyWeather.getHourlyWeather().isEmpty()) {
        return ResponseEntity.noContent().build();
      }

      return ResponseEntity.ok(hourlyWeather);

    } catch (LocationNotFoundException e) {
      return ResponseEntity.notFound().build();
    } catch (NumberFormatException e) {
      log.error(e.getMessage(), e);
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping("/{locationCode}")
  public ResponseEntity<ListHourlyWeatherResponse> updateHourlyForecast(
        @PathVariable String locationCode,
        @RequestBody @Valid List<HourlyWeatherRequest> request
  ) {
    log.info("Location code: {}", locationCode);
    log.info("Hourly weather: {}", request);

    if (request.isEmpty()) {
      throw new BadRequestException("Hourly forecast data cannot be empty");
    }

    try {
      ListHourlyWeatherResponse hourlyWeather = hourlyWeatherService.updateHourlyWeather(locationCode, request);

      return ResponseEntity.ok(hourlyWeather);
    } catch (LocationNotFoundException e) {
      return ResponseEntity.notFound().build();
    }

  }
}
