package com.skyapi.weather.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weather.common.dto.request.RealtimeWeatherRequest;
import com.skyapi.weather.common.dto.response.RealTimeWeatherResponse;
import com.skyapi.weather.common.entity.Location;
import com.skyapi.weather.common.entity.RealtimeWeather;
import com.skyapi.weather.service.exception.GeolocationNotFoundException;
import com.skyapi.weather.service.exception.LocationNotFoundException;
import com.skyapi.weather.service.service.GeolocationService;
import com.skyapi.weather.service.service.RealtimeWeatherService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(RealtimeWeatherController.class)
class RealtimeWeatherControllerTests {

  private static final String END_POINT_PATH = "/api/v1/realtime";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private RealtimeWeatherService realtimeWeatherService;

  @MockBean
  private GeolocationService geolocationService;

  @Test
  void testGetShouldReturn400BadRequest() throws Exception {

    Mockito.when(geolocationService.getLocation(Mockito.anyString()))
          .thenThrow(new GeolocationNotFoundException("Location not found"));

    mockMvc.perform(get(END_POINT_PATH))
          .andExpect(status().isBadRequest())
          .andDo(print());
  }

  @Test
  void testGetShouldReturn404BadRequest() throws Exception {

    Location location = new Location();

    Mockito.when(geolocationService.getLocation(Mockito.anyString()))
          .thenReturn(location);

    Mockito.when(realtimeWeatherService.getByLocation(Mockito.any()))
          .thenThrow(new LocationNotFoundException("Location not found"));

    mockMvc.perform(get(END_POINT_PATH))
          .andExpect(status().isNotFound())
          .andDo(print());
  }

  @Test
  void testGetShouldReturn200Ok() throws Exception {

    Location location = Location.builder()
          .code("123456")
          .cityName("City")
          .regionName("Region")
          .countryName("Country")
          .countryCode("CO")
          .enabled(true)
          .trashed(false)
          .build();

    Mockito.when(geolocationService.getLocation(Mockito.anyString())).thenReturn(location);

    RealtimeWeather realtimeWeather = RealtimeWeather.builder()
          .temperature(25)
          .humidity(33)
          .precipitation(1013)
          .build();

    realtimeWeather.setLocation(location);
    location.setRealtimeWeather(realtimeWeather);

    Mockito.when(realtimeWeatherService.getByLocation(location))
          .thenReturn(entity2DTO(realtimeWeather));

    String expectedLocation = location.getCityName() + ", " + location.getCountryName();

    mockMvc.perform(get(END_POINT_PATH))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.location", org.hamcrest.Matchers.is(expectedLocation)))
          .andDo(print());
  }

  @Test
  void testGetRealtimeWeatherByLocationCodeShouldReturn404NotFound() throws Exception {
    Mockito.when(realtimeWeatherService.getByLocationCode("ABC_USA"))
          .thenThrow(new LocationNotFoundException("Location not found"));

    mockMvc.perform(get(END_POINT_PATH + "/ABC_USA"))
          .andExpect(status().isNotFound())
          .andDo(print());
  }

  @Test
  void testGetRealtimeWeatherByLocationCodeShouldReturn200Ok() throws Exception {
    Location location = Location.builder()
          .code("NYC_USA")
          .cityName("City")
          .regionName("Region")
          .countryName("Country")
          .countryCode("CO")
          .enabled(true)
          .trashed(false)
          .build();

    RealtimeWeather realtimeWeather = RealtimeWeather.builder()
          .temperature(25)
          .humidity(33)
          .precipitation(1013)
          .build();

    realtimeWeather.setLocation(location);
    location.setRealtimeWeather(realtimeWeather);

    Mockito.when(realtimeWeatherService.getByLocationCode("NYC_USA"))
          .thenReturn(entity2DTO(realtimeWeather));

    mockMvc.perform(get(END_POINT_PATH + "/NYC_USA"))
          .andExpect(status().isOk())
          .andDo(print());
  }

  @Test
  void testUpdateRealtimeWeatherShouldReturn404NotFound() throws Exception {

    RealtimeWeatherRequest request = RealtimeWeatherRequest.builder()
          .temperature(25)
          .humidity(33)
          .precipitation(100)
          .status("Cloudy")
          .build();

    Mockito.when(realtimeWeatherService.update("ABC_USA", request))
          .thenThrow(new LocationNotFoundException("Location not found"));

    String requestJson = mapper.writeValueAsString(request);

    mockMvc.perform(put(END_POINT_PATH + "/ABC_USA").content(requestJson).contentType("application/json"))
          .andExpect(status().isNotFound())
          .andDo(print());
  }

  @Test
  void testUpdateRealtimeWeatherShouldReturn400BadRequest() throws Exception {

    RealtimeWeatherRequest request = RealtimeWeatherRequest.builder()
          .temperature(100)
          .humidity(200)
          .precipitation(200)
          .windSpeed(300)
          .build();

    String requestJson = mapper.writeValueAsString(request);

    MvcResult result = mockMvc.perform(put(END_POINT_PATH + "/ABC_USA").content(requestJson).contentType("application/json"))
          .andExpect(status().isBadRequest())
          .andDo(print())
          .andReturn();

    String content = result.getResponse().getContentAsString();

    assertThat(content, allOf(
          containsString("Temperature must be between -50 and 50"),
          containsString("Humidity must be between 0 and 100"),
          containsString("Precipitation must be between 0 and 100"),
          containsString("Status cannot be null"),
          containsString("Wind speed must be between 0 and 200")
    ));
  }

  @Test
  void testUpdateRealtimeWeatherShouldReturn200Ok() throws Exception {

    Location location = Location.builder()
          .code("NYC_USA")
          .cityName("City")
          .regionName("Region")
          .countryName("Country")
          .countryCode("CO")
          .enabled(true)
          .trashed(false)
          .build();

    RealtimeWeatherRequest request = RealtimeWeatherRequest.builder()
          .temperature(25)
          .humidity(33)
          .precipitation(40)
          .status("Cloudy")
          .windSpeed(10)
          .build();

    RealtimeWeather realtimeWeather = RealtimeWeather.builder()
          .temperature(35)
          .humidity(43)
          .precipitation(50)
          .status("Sunny")
          .windSpeed(20)
          .location(location)
          .build();


    Mockito.when(realtimeWeatherService.update("NYC_USA", request)).thenReturn(entity2DTO(realtimeWeather));

    String requestJson = mapper.writeValueAsString(request);

    mockMvc.perform(put(END_POINT_PATH + "/NYC_USA").content(requestJson).contentType("application/json"))
          .andExpect(status().isOk())
          .andDo(print());
  }

  private RealTimeWeatherResponse entity2DTO(RealtimeWeather weather) {
    return RealTimeWeatherResponse.builder()
          .location(weather.getLocation().getCityName() + ", " + weather.getLocation().getCountryName())
          .temperature(weather.getTemperature())
          .humidity(weather.getHumidity())
          .precipitation(weather.getPrecipitation())
          .windSpeed(weather.getWindSpeed())
          .status(weather.getStatus())
          .lastUpdated(weather.getLastUpdated())
          .build();
  }

}
