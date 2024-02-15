package com.skyapi.weather.service.service;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.skyapi.weather.common.entity.Location;
import com.skyapi.weather.service.exception.GeolocationNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class GeolocationService {

  private final String DBPath = "/ip2locdb/IP2LOCATION-LITE-DB3.BIN";
  private final IP2Location ipLocator = new IP2Location();

  public GeolocationService() {
    try {
      InputStream inputStream = getClass().getResourceAsStream(DBPath);
      byte[] data = inputStream.readAllBytes();
      ipLocator.Open(data);
      inputStream.close();
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  public Location getLocation(String ipAddress) throws GeolocationNotFoundException {
    try {
      IPResult ipResult = ipLocator.IPQuery(ipAddress);
      if (!ipResult.getStatus().equals("OK")) {
        throw new GeolocationNotFoundException("Geolocation failed with status: " + ipResult.getStatus());
      }

      log.info("Location found: {}", ipResult);
      return Location.builder()
            .cityName(ipResult.getCity())
            .regionName(ipResult.getRegion())
            .countryName(ipResult.getCountryLong())
            .countryCode(ipResult.getCountryShort())
            .build();
    } catch (IOException e) {
      throw new GeolocationNotFoundException("Error querying IP database");
    }
  }
}
