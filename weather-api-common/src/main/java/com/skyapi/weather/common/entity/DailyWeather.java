package com.skyapi.weather.common.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "daily_weather")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyWeather {

  @EmbeddedId
  private DailyWeatherId id;

  private int minTemp;
  private int maxTemp;
  private int precipitation;

  private String status;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DailyWeather that = (DailyWeather) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  public DailyWeather copy() {
    return DailyWeather.builder()
          .id(this.id)
          .build();
  }
}
