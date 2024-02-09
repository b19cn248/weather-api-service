package com.skyapi.weather.common.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ListHourlyWeatherResponse {
  private String location;
  private List<HourlyWeatherResponse> hourlyWeather = new ArrayList<>();

  public void addHourlyWeather(HourlyWeatherResponse hourlyWeatherResponse) {
    this.hourlyWeather.add(hourlyWeatherResponse);
  }
}
