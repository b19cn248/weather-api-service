package com.skyapi.weather.service.constant;

public class CommonConstant {

  private CommonConstant() {
  }

  public static class Constants {

    private Constants() {
    }

    public static final String HOUR_HEADING = "X-Current-Hour";
  }

  public static class StatusException {
    public static final Integer NOT_FOUND = 404;
    public static final Integer CONFLICT = 409;
    public static final Integer BAD_REQUEST = 400;
    public static final Integer INTERNAL_SERVER_ERROR = 500;
  }

}
