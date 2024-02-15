package com.skyapi.weather.service.exception.base;


public class NotFoundException extends RuntimeException {

  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException() {
    super("Not Found");
  }
}
