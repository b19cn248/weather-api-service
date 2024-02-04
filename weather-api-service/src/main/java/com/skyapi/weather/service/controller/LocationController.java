package com.skyapi.weather.service.controller;

import com.skyapi.weather.common.dto.request.LocationRequest;
import com.skyapi.weather.common.dto.request.LocationUpdateRequest;
import com.skyapi.weather.common.dto.response.LocationResponse;
import com.skyapi.weather.service.exception.LocationNotFoundException;
import com.skyapi.weather.service.service.LocationService;
import com.skyapi.weather.service.utils.CommonUtility;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/locations")
@Tag(name = "LOCATIONS")
public class LocationController {

  private final LocationService locationService;

  @PostMapping
  public ResponseEntity<LocationResponse> create(@RequestBody @Valid LocationRequest request) {
    LocationResponse response = locationService.create(request);

    URI uri = URI.create("/api/v1/locations/" + response.getCode());

    return ResponseEntity.created(uri).body(response);
  }

  @GetMapping
  public ResponseEntity<List<LocationResponse>> list(HttpServletRequest request) {
    log.info("(List all locations)");

    List<LocationResponse> locations = locationService.list();

    if (locations.isEmpty()) {
      return ResponseEntity.noContent().build();
    }

    String ipAddress = CommonUtility.getIPAddress(request);

    log.info("IP Address: {}", ipAddress);

    return ResponseEntity.ok(locations);
  }

  @GetMapping("{code}")
  public ResponseEntity<LocationResponse> detail(@PathVariable String code) {
    log.info("(detail) code:{}", code);

    try {
      return ResponseEntity.ok(locationService.detail(code));
    } catch (LocationNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("/{code}")
  public ResponseEntity<LocationResponse> update(
        @PathVariable String code,
        @RequestBody @Valid LocationUpdateRequest request
  ) {
    log.info("(update) code:{}, request:{}", code, request);

    try {
      return ResponseEntity.ok(locationService.update(code, request));
    } catch (LocationNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{code}")
  public ResponseEntity<Void> delete(@PathVariable String code) {
    log.info("(delete) code:{}", code);

    try {
      locationService.delete(code);
      return ResponseEntity.noContent().build();
    } catch (LocationNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }


}
