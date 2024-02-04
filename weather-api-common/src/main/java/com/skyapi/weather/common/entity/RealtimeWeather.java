package com.skyapi.weather.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "realtime_weather")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RealtimeWeather {

  @Id
  @Column(name = "location_code")
  private String locationCode;
  private int temperature;
  private int humidity;
  private int precipitation;
  private int windSpeed;

  @Column(length = 50)
  private String status;
  private Date lastUpdated;

  @OneToOne
  @JoinColumn(name = "location_code", referencedColumnName = "code")
  @MapsId
  private Location location;

  public void setLocation(Location location) {
    this.setLocationCode(location.getCode());
    this.location = location;
  }
}
