package com.skyapi.weather.service.service;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.skyapi.weather.common.entity.Location;
import com.skyapi.weather.service.exception.GeolocationNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class GeolocationService {

  private static final String path = "ip2locdb/IP2LOCATION-LITE-DB3.BIN";
  private static final IP2Location ip2Location = new IP2Location();

  public GeolocationService() {
    try {
      ip2Location.Open(path);
    } catch (IOException e) {
      log.error("Error opening IP2Location database", e);
    }

  }

  public Location getLocation(String ipAddress) {
    try {
      IPResult ipResult = ip2Location.IPQuery(ipAddress);

      if (ipResult.getStatus().equals("OK")) {
        return Location.builder()
              .cityName(ipResult.getCity())
              .regionName(ipResult.getRegion())
              .countryName(ipResult.getCountryLong())
              .countryCode(ipResult.getCountryShort())
              .build();
      } else {
        throw new GeolocationNotFoundException("Invalid IP address");
      }
    } catch (IOException e) {
      throw new GeolocationNotFoundException("Error querying IP2Location database", e);
    }
  }
}
