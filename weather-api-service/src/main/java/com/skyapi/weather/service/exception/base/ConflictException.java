package com.skyapi.weather.service.exception.base;


public class ConflictException extends RuntimeException {

  public ConflictException(String message) {
    super(message);
  }

  public ConflictException() {
    super("Conflict");
  }
}
