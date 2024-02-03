package com.skyapi.weather.service.service.impl;

import com.skyapi.weather.common.dto.request.LocationRequest;
import com.skyapi.weather.common.dto.request.LocationUpdateRequest;
import com.skyapi.weather.common.dto.response.LocationResponse;
import com.skyapi.weather.common.entity.Location;
import com.skyapi.weather.service.exception.LocationNotFoundException;
import com.skyapi.weather.service.repository.LocationRepository;
import com.skyapi.weather.service.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationServiceImpl implements LocationService {

    private final LocationRepository repository;


    @Override
    public LocationResponse create(LocationRequest request) {
        log.info("(create) request:{}", request);

        Location location = toEntity(request);

        return toDTO(repository.save(location));
    }

    @Override
    public List<LocationResponse> list() {

        log.info("(List all locations)");

        return repository.list();
    }

    @Override
    public LocationResponse detail(String code) {
        log.info("(detail) code:{}", code);
        return repository.detail(code).orElseThrow(LocationNotFoundException::new);
    }

    @Override
    public LocationResponse update(String code, LocationUpdateRequest request) {
        log.info("(update) code:{}, request:{}", code, request);

        Location locationInDB = repository.findByCode(code).orElseThrow(LocationNotFoundException::new);

        locationInDB.setCityName(request.getCityName());
        locationInDB.setRegionName(request.getRegionName());
        locationInDB.setCountryCode(request.getCountryCode());
        locationInDB.setCountryName(request.getCountryName());
        locationInDB.setEnabled(true);

        return toDTO(repository.save(locationInDB));
    }

    @Override
    public void delete(String code) {
        log.info("(delete) code: {}", code);

        if (!repository.existsById(code)) {
            throw new LocationNotFoundException();
        }
        repository.softDelete(code);
    }


    private Location toEntity(LocationRequest request) {

        log.debug("(toEntity) request:{}", request);

        return Location.builder()
                .code(request.getCode())
                .cityName(request.getCityName())
                .regionName(request.getRegionName())
                .countryName(request.getCountryName())
                .countryCode(request.getCountryCode())
                .enabled(true)
                .trashed(false)
                .build();
    }

    private LocationResponse toDTO(Location location) {

        log.debug("(toDTO) location:{}", location);

        return LocationResponse.builder()
                .code(location.getCode())
                .cityName(location.getCityName())
                .regionName(location.getRegionName())
                .countryName(location.getCountryName())
                .countryCode(location.getCountryCode())
                .enabled(location.isEnabled())
                .build();
    }
}
