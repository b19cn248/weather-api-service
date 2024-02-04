package com.skyapi.weather.common.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RealtimeWeatherRequest {

  @Range(min = -50, max = 50, message = "Temperature must be between -50 and 50")
  private int temperature;

  @Range(min = 0, max = 100, message = "Humidity must be between 0 and 100")
  private int humidity;

  @Range(min = 0, max = 100, message = "Precipitation must be between 0 and 100")
  private int precipitation;

  @NotNull(message = "Status cannot be null")
  @Length(min = 3, max = 50, message = "Status must be between 3 and 50 characters")
  private String status;

  @Range(min = 0, max = 200, message = "Wind speed must be between 0 and 200")
  private int windSpeed;
}
