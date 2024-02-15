package com.skyapi.weather.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weather.common.dto.response.FullWeatherResponse;
import com.skyapi.weather.common.entity.*;
import com.skyapi.weather.service.configuration.ServiceConfigurationTests;
import com.skyapi.weather.service.exception.GeolocationNotFoundException;
import com.skyapi.weather.service.exception.LocationNotFoundException;
import com.skyapi.weather.service.service.FullWeatherService;
import com.skyapi.weather.service.service.GeolocationService;
import com.skyapi.weather.service.service.MapperService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(FullWeatherController.class)
@ContextConfiguration(classes = {ServiceConfigurationTests.class})
public class FullWeatherControllerTests {

  private final String URL = "/api/v1/full";

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private FullWeatherService fullWeatherService;
  @MockBean
  private GeolocationService geolocationService;
  @Autowired
  private MapperService mapperService;

  @Test
  void testGetByIPAddressShouldReturn400BadRequestBecauseOfInvalidIPAddress() throws Exception {

    GeolocationNotFoundException exception = new GeolocationNotFoundException("Invalid IP Address");

    Mockito.when(geolocationService.getLocation(Mockito.anyString())).
          thenThrow(exception);

    mockMvc.perform(get(URL))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.errors[0]", is(exception.getMessage())))
          .andDo(print());

  }

  @Test
  void testGetByIPAddressShouldReturn404NotFoundBecauseOfGeolocationNotFound() throws Exception {

    LocationNotFoundException exception = new LocationNotFoundException("Location not found");

    Mockito.when(geolocationService.getLocation(Mockito.anyString())).
          thenThrow(exception);

    mockMvc.perform(get(URL))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.errors[0]", is(exception.getMessage())))
          .andDo(print());

  }

  @Test
  void testGetByIPAddressShouldReturn200OK() throws Exception {

    Location location = Location.builder()
          .code("NYC_USA")
          .cityName("New York")
          .regionName("New York")
          .countryName("United States")
          .countryCode("US")
          .build();

    DailyWeather dailyWeather1 = DailyWeather.builder()
          .id(new DailyWeatherId(10, 2, location))
          .minTemp(30)
          .maxTemp(40)
          .precipitation(10)
          .status("Sunny")
          .build();
    DailyWeather dailyWeather2 = DailyWeather.builder()
          .id(new DailyWeatherId(11, 2, location))
          .minTemp(26)
          .maxTemp(35)
          .precipitation(20)
          .status("Cloudy")
          .build();

    HourlyWeather hourlyWeather1 = HourlyWeather.builder()
          .id(new HourlyWeatherId(10, location))
          .temperature(30)
          .precipitation(20)
          .status("Sunny")
          .build();

    HourlyWeather hourlyWeather2 = HourlyWeather.builder()
          .id(new HourlyWeatherId(11, location))
          .temperature(30)
          .precipitation(40)
          .status("Cloudy")
          .build();

    RealtimeWeather realtimeWeather = RealtimeWeather.builder()
          .location(location)
          .temperature(30)
          .precipitation(20)
          .status("Sunny")
          .build();

    location.setRealtimeWeather(realtimeWeather);
    location.setDailyWeathers(List.of(dailyWeather1, dailyWeather2));
    location.setHourlyWeathers(List.of(hourlyWeather1, hourlyWeather2));

    Mockito.when(geolocationService.getLocation(Mockito.anyString())).
          thenReturn(location);

    Mockito.when(fullWeatherService.getFullWeather(location)).thenReturn(
          FullWeatherResponse.builder()
                .location(location.toString())
                .realTimeWeather(mapperService.entity2DTO(realtimeWeather))
                .dailyForecast(mapperService.convertToResponse(List.of(dailyWeather1, dailyWeather2), location))
                .hourlyForecast(mapperService.convertToResponse(List.of(hourlyWeather1, hourlyWeather2)))
                .build()
    );

    mockMvc.perform(get(URL))
          .andExpect(status().isOk())
          .andDo(print());

  }
}
