package com.skyapi.weather.common.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LocationRequest {

  @NotNull(message = "Code is mandatory")
  @Length(min = 3, max = 12, message = "Code must be between 3 and 12 characters")
  private String code;

  @NotNull(message = "City name is mandatory")
  @Length(min = 3, max = 128, message = "City name must be between 3 and 128 characters")
  private String cityName;

  @Length(min = 3, max = 128, message = "Region name must be between 3 and 128 characters")
  private String regionName;

  @NotNull(message = "Country name is mandatory")
  @Length(min = 3, max = 64, message = "Country name must be between 3 and 64 characters")
  private String countryName;

  @NotNull(message = "Country code is mandatory")
  @Length(min = 2, max = 2, message = "Country code must be 2 characters")
  private String countryCode;

  private boolean enabled;
  private boolean trashed;
}
