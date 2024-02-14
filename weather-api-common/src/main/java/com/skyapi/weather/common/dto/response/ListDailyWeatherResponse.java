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
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class ListDailyWeatherResponse {
  private String location;
  private List<DailyWeatherResponse> dailyWeather = new ArrayList<>();

  public void addDailyWeather(DailyWeatherResponse dailyWeatherResponse) {
    this.dailyWeather.add(dailyWeatherResponse);
  }
}
