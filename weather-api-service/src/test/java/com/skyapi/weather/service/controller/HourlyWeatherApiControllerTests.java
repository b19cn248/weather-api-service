package com.skyapi.weather.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weather.common.dto.request.HourlyWeatherRequest;
import com.skyapi.weather.common.dto.response.HourlyWeatherResponse;
import com.skyapi.weather.common.dto.response.ListHourlyWeatherResponse;
import com.skyapi.weather.common.entity.HourlyWeather;
import com.skyapi.weather.common.entity.HourlyWeatherId;
import com.skyapi.weather.common.entity.Location;
import com.skyapi.weather.service.exception.GeolocationNotFoundException;
import com.skyapi.weather.service.exception.LocationNotFoundException;
import com.skyapi.weather.service.service.GeolocationService;
import com.skyapi.weather.service.service.HourlyWeatherService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HourlyWeatherController.class)
class HourlyWeatherApiControllerTests {
  private static final String ENDPOINT = "/api/v1/hourly";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private GeolocationService geolocationService;

  @MockBean
  private HourlyWeatherService hourlyWeatherService;

  @Autowired
  private ObjectMapper mapper;

  @Test
  void testGetIPShouldReturn400BadRequestBecauseNoHeaderXCurrentHour() throws Exception {

    mockMvc.perform(get(ENDPOINT))
          .andExpect(status().isBadRequest())
          .andDo(print());
  }

  @Test
  void testGetIPShouldReturn400BadRequestBecauseGeolocationException() throws Exception {

    Mockito.when(geolocationService.getLocation(Mockito.anyString()))
          .thenThrow(new GeolocationNotFoundException("Geolocation failed with status: 400"));

    mockMvc.perform(get(ENDPOINT).header("X-Current-Hour", "1"))
          .andExpect(status().isBadRequest())
          .andDo(print());
  }

  @Test
  void testGetIPShouldReturn204NoContent() throws Exception {

    int currentHour = 1;
    String locationCode = "NYC_USA";

    Location location = Location.builder().code(locationCode).build();

    Mockito.when(geolocationService.getLocation(Mockito.anyString()))
          .thenReturn(location);

    Mockito.when(hourlyWeatherService.getHourlyWeather(location, currentHour))
          .thenReturn(new ListHourlyWeatherResponse());

    mockMvc.perform(get(ENDPOINT).header("X-Current-Hour", String.valueOf(currentHour)))
          .andExpect(status().isNoContent())
          .andDo(print());
  }

  @Test
  void testGetIPShouldReturn200OK() throws Exception {

    Location location = Location.builder().code("NYC_USA")
          .cityName("New York City")
          .regionName("New York")
          .countryCode("US")
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

    List<HourlyWeather> hourlyWeathers = new ArrayList<>();
    hourlyWeathers.add(hourlyWeather1);
    hourlyWeathers.add(hourlyWeather2);

    location.setHourlyWeathers(hourlyWeathers);

    int currentHour = 1;

    Mockito.when(geolocationService.getLocation(Mockito.anyString()))
          .thenReturn(location);

    Mockito.when(hourlyWeatherService.getHourlyWeather(location, currentHour))
          .thenReturn(ListHourlyWeatherResponse.builder()
                .location(location.toString())
                .hourlyWeather(List.of(
                      HourlyWeatherResponse.builder()
                            .hourOfDay(hourlyWeather1.getId().getHourOfDay())
                            .temperature(hourlyWeather1.getTemperature())
                            .precipitation(hourlyWeather1.getPrecipitation())
                            .status(hourlyWeather1.getStatus())
                            .build(),
                      HourlyWeatherResponse.builder()
                            .hourOfDay(hourlyWeather2.getId().getHourOfDay())
                            .temperature(hourlyWeather2.getTemperature())
                            .precipitation(hourlyWeather2.getPrecipitation())
                            .status(hourlyWeather2.getStatus())
                            .build()
                ))
                .build());

    String expectedResponse = location.toString();

    mockMvc.perform(get(ENDPOINT).header("X-Current-Hour", String.valueOf(currentHour)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.location", CoreMatchers.is(expectedResponse)))
          .andExpect(jsonPath("$.hourly_weather[0].hour_of_day", CoreMatchers.is(hourlyWeather1.getId().getHourOfDay())))
          .andDo(print());
  }

  @Test
  void testUpdateShouldReturn400BadRequestBecauseNoData() throws Exception {
    String locationCode = "NYC_USA";
    String requestURI = ENDPOINT + "/" + locationCode;

    List<HourlyWeatherRequest> hourlyWeathers = new ArrayList<>();

    String requestBody = mapper.writeValueAsString(hourlyWeathers);

    mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.errors[0]", CoreMatchers.is("Hourly forecast data cannot be empty")))
          .andDo(print());
  }

  @Test
  void testUpdateShouldReturn400BadRequestBecauseInvalidData() throws Exception {
    String locationCode = "NYC_USA";
    String requestURI = ENDPOINT + "/" + locationCode;

    HourlyWeatherRequest hourlyWeather1 = HourlyWeatherRequest.builder()
          .hourOfDay(10)
          .temperature(301)
          .precipitation(20)
          .status("Sunny")
          .build();

    HourlyWeatherRequest hourlyWeather2 = HourlyWeatherRequest.builder()
          .hourOfDay(11)
          .temperature(30)
          .precipitation(40)
          .status("Cloudy")
          .build();

    List<HourlyWeatherRequest> hourlyWeathers = List.of(hourlyWeather1, hourlyWeather2);

    String requestBody = mapper.writeValueAsString(hourlyWeathers);

    mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.errors[0]", containsString("Temperature must be between -100 and 100")))
          .andDo(print());
  }

  @Test
  void testUpdateShouldReturn404NotFound() throws Exception {
    String locationCode = "NYC_USA";
    String requestURI = ENDPOINT + "/" + locationCode;

    HourlyWeatherRequest hourlyWeather1 = HourlyWeatherRequest.builder()
          .hourOfDay(10)
          .temperature(30)
          .precipitation(20)
          .status("Sunny")
          .build();

    HourlyWeatherRequest hourlyWeather2 = HourlyWeatherRequest.builder()
          .hourOfDay(11)
          .temperature(30)
          .precipitation(40)
          .status("Cloudy")
          .build();

    List<HourlyWeatherRequest> hourlyWeathers = List.of(hourlyWeather1, hourlyWeather2);

    Mockito.when(hourlyWeatherService.updateHourlyWeather(locationCode, hourlyWeathers))
          .thenThrow(new LocationNotFoundException("Location not found for code: " + locationCode));

    String requestBody = mapper.writeValueAsString(hourlyWeathers);

    mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
          .andExpect(status().isNotFound())
          .andDo(print());
  }

  @Test
  void testUpdateShouldReturn200OK() throws Exception {

    Location location = Location.builder().code("NYC_USA")
          .cityName("New York City")
          .regionName("New York")
          .countryCode("US")
          .countryName("United States")
          .build();

    String requestURI = ENDPOINT + "/" + location.getCode();

    HourlyWeatherRequest hourlyWeather1 = HourlyWeatherRequest.builder()
          .hourOfDay(10)
          .temperature(30)
          .precipitation(20)
          .status("Sunny")
          .build();

    HourlyWeatherRequest hourlyWeather2 = HourlyWeatherRequest.builder()
          .hourOfDay(11)
          .temperature(30)
          .precipitation(40)
          .status("Cloudy")
          .build();

    List<HourlyWeatherRequest> hourlyWeathers = List.of(hourlyWeather1, hourlyWeather2);

    Mockito.when(hourlyWeatherService.updateHourlyWeather(location.getCode(), hourlyWeathers))
          .thenReturn(ListHourlyWeatherResponse.builder()
                .hourlyWeather(List.of(
                      HourlyWeatherResponse.builder()
                            .hourOfDay(hourlyWeather1.getHourOfDay())
                            .temperature(hourlyWeather1.getTemperature())
                            .precipitation(hourlyWeather1.getPrecipitation())
                            .status(hourlyWeather1.getStatus())
                            .build(),
                      HourlyWeatherResponse.builder()
                            .hourOfDay(hourlyWeather2.getHourOfDay())
                            .temperature(hourlyWeather2.getTemperature())
                            .precipitation(hourlyWeather2.getPrecipitation())
                            .status(hourlyWeather2.getStatus())
                            .build()
                ))
                .location(location.toString())
                .build());

    String requestBody = mapper.writeValueAsString(hourlyWeathers);

    mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.location", CoreMatchers.is(location.toString())))
          .andExpect(jsonPath("$.hourly_weather[0].hour_of_day", CoreMatchers.is(hourlyWeather1.getHourOfDay())))
          .andDo(print());
  }
}
