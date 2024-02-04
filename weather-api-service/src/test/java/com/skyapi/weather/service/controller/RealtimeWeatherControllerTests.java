package com.skyapi.weather.service.controller;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(RealtimeWeatherController.class)
class RealtimeWeatherControllerTests {

  private static final String END_POINT_PATH = "/api/v1/realtime";

  @Autowired
  private MockMvc mockMvc;

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

    mockMvc.perform(get(END_POINT_PATH))
          .andExpect(status().isOk())
          .andDo(print());
  }

  private RealTimeWeatherResponse entity2DTO(RealtimeWeather weather) {
    return RealTimeWeatherResponse.builder()
          .temperature(weather.getTemperature())
          .humidity(weather.getHumidity())
          .precipitation(weather.getPrecipitation())
          .windSpeed(weather.getWindSpeed())
          .lastUpdated(weather.getLastUpdated())
          .build();
  }
}
