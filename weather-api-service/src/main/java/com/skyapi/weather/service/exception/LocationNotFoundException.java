package com.skyapi.weather.service.exception;

import com.skyapi.weather.service.exception.base.NotFoundException;

public class LocationNotFoundException extends NotFoundException {

  public LocationNotFoundException() {
    super("Location not Found");
  }

  public LocationNotFoundException(String message) {
    super(message);
  }


}
