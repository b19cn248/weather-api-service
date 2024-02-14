package com.skyapi.weather.service.exception;

public class LocationNotFoundException extends RuntimeException {

    public LocationNotFoundException() {
        super("Location not Found");
    }

    public LocationNotFoundException(String message) {
        super(message);
    }


}
