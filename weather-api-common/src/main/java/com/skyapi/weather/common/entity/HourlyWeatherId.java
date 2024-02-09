package com.skyapi.weather.common.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HourlyWeatherId implements Serializable {
  private int hourOfDay;

  @ManyToOne
  @JoinColumn(name = "location_code")
  private Location location;
}
