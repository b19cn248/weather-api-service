package com.skyapi.weather.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

  @Id
  @Column(length = 12, nullable = false, unique = true)
  private String code;

  @Column(length = 128, nullable = false)
  private String cityName;

  @Column(length = 128)
  private String regionName;

  @Column(length = 64, nullable = false)
  private String countryName;

  @Column(length = 64, nullable = false)
  private String countryCode;

  private boolean enabled;
  private boolean trashed;

  @OneToOne(mappedBy = "location", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @PrimaryKeyJoinColumn
  @JsonIgnore
  @EqualsAndHashCode.Exclude
  private RealtimeWeather realtimeWeather;

  @OneToMany(mappedBy = "id.location", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<HourlyWeather> hourlyWeathers = new ArrayList<>();

  @OneToMany(mappedBy = "id.location", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DailyWeather> dailyWeathers = new ArrayList<>();

  @Override
  public String toString() {
    return cityName + ", " + (((Objects.nonNull(regionName))) ? regionName + ", " : "") + countryName;
  }
}
