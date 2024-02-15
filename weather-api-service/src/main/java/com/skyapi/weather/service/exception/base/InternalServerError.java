package com.skyapi.weather.service.exception.base;

public class InternalServerError extends RuntimeException {

  public InternalServerError(String message) {
    super(message);
  }

  public InternalServerError() {
    super("Internal Server Error");
  }
}
