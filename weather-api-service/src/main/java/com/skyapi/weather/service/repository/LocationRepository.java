package com.skyapi.weather.service.repository;

import com.skyapi.weather.common.dto.response.LocationResponse;
import com.skyapi.weather.common.entity.Location;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, String> {

  @Query(" select new com.skyapi.weather.common.dto.response.LocationResponse(" +
        " l.code, l.cityName, l.regionName, l.countryName, l.countryCode, l.enabled )" +
        " from Location l where l.trashed = false ")
  List<LocationResponse> list();

  @Query(" select new com.skyapi.weather.common.dto.response.LocationResponse(" +
        " l.code, l.cityName, l.regionName, l.countryName, l.countryCode, l.enabled)" +
        " from Location l where l.trashed = false  and l.code = :code")
  Optional<LocationResponse> detail(String code);

  @Query(value = "select l from Location l where l.code = :code and l.trashed = false ")
  Optional<Location> findByCode(String code);

  @Query(value = "update Location l set l.trashed = true where l.code = :code")
  @Modifying
  @Transactional
  void softDelete(String code);

  @Query(value = "select l from Location l where l.countryCode = :countryCode " +
        "and l.cityName = :cityName and l.trashed = false ")
  Location findByCountryCodeAndCityName(String countryCode, String cityName);
}
