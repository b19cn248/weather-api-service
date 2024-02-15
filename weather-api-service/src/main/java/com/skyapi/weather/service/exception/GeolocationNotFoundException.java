package com.skyapi.weather.service.exception;

import com.skyapi.weather.service.exception.base.BadRequestException;

public class GeolocationNotFoundException extends BadRequestException {
  public GeolocationNotFoundException(String message) {
    super(message);
  }
}
