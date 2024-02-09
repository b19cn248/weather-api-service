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
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class HourlyWeatherRequest {

  private int hourOfDay;

  @Range(min = -100, max = 100, message = "Temperature must be between -100 and 100")
  private int temperature;

  @Range(min = 0, max = 100, message = "Humidity must be between 0 and 100")
  private int precipitation;

  @NotNull(message = "Status cannot be null")
  @Length(min = 3, max = 64, message = "Status must have 3-64 characters")
  private String status;
}
