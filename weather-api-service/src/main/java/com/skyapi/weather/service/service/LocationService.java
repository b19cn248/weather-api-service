package com.skyapi.weather.service.service;

import com.skyapi.weather.common.dto.request.LocationRequest;
import com.skyapi.weather.common.dto.request.LocationUpdateRequest;
import com.skyapi.weather.common.dto.response.LocationResponse;

import java.util.List;

public interface LocationService {

    LocationResponse create(LocationRequest request);

    List<LocationResponse> list();

    LocationResponse detail(String code);

    LocationResponse update(String code, LocationUpdateRequest request);

    void delete(String code);
}
