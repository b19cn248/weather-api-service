package com.skyapi.weather.common.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "hourly_weather")
public class HourlyWeather {

  @EmbeddedId
  private HourlyWeatherId id;

  private int temperature;

  private int precipitation;

  private String status;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HourlyWeather that = (HourlyWeather) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  public HourlyWeather getShallowCopy() {
    HourlyWeather copy = new HourlyWeather();
    copy.setId(this.getId());
    return copy;
  }
}
