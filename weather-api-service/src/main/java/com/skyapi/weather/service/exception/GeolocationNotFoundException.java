package com.skyapi.weather.service.exception;

public class GeolocationNotFoundException extends RuntimeException{
    public GeolocationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeolocationNotFoundException(String message) {
        super(message);
    }
}
