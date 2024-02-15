package com.skyapi.weather.common.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class FullWeatherResponse {
  private String location;
  private RealTimeWeatherResponse realTimeWeather;
  private List<HourlyWeatherResponse> hourlyForecast;
  private List<DailyWeatherResponse> dailyForecast;
}
