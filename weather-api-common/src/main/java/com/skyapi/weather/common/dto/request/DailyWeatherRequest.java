package com.skyapi.weather.common.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class DailyWeatherRequest {

  @Range(min = 1, max = 31, message = "Day of month must be between 1 and 31")
  private int dayOfMonth;

  @Range(min = 1, max = 12, message = "Month must be between 1 and 12")
  private int month;

  @Range(min = -100, max = 100, message = "Min temperature must be between -100 and 100")
  private int minTemp;

  @Range(min = -100, max = 100, message = "Max temperature must be between -100 and 100")
  private int maxTemp;

  @Range(min = 0, max = 100, message = "Precipitation must be between 0 and 100")
  private int precipitation;

  @Range(min = 3, max = 64, message = "Status must have 3-64 characters")
  private String status;

}
