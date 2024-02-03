package com.skyapi.weather.common.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LocationResponse {
    private String code;
    private String cityName;
    private String regionName;
    private String countryName;
    private String countryCode;
    private boolean enabled;


    public LocationResponse(
            String code,
            String cityName,
            String regionName,
            String countryName,
            String countryCode,
            boolean enabled
    ) {
        this.code = code;
        this.cityName = cityName;
        this.regionName = regionName;
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.enabled = enabled;
    }
}
