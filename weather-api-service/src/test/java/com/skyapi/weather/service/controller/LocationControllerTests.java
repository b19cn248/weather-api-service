package com.skyapi.weather.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weather.common.dto.request.LocationRequest;
import com.skyapi.weather.common.dto.request.LocationUpdateRequest;
import com.skyapi.weather.common.dto.response.LocationResponse;
import com.skyapi.weather.service.exception.LocationNotFoundException;
import com.skyapi.weather.service.service.LocationService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(LocationController.class)
class LocationControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private LocationService locationService;

  private static final String END_POINT_PATH = "/api/v1/locations";

  private static final LocationResponse NYC_USA_RESPONSE = fakeNycUsa();
  private static final LocationResponse DELHI_IN_RESPONSE = fakeDelhiIn();
  private static final LocationRequest CREATE_NYC_USA_REQUEST = fakeCreatedRequest();
  private static final LocationUpdateRequest UPDATE_NYC_USA_REQUEST = fakeUpdatedRequest();
  private static final LocationRequest FAKE_REQUEST_INVALID = fakeRequestInvalid();

  private static final LocationRequest FAKE_REQUEST_INVALID_LENGTH = fakeRequestInvalidLength();

  private static final String CODE_IS_NOT_PRESENT = "ABCD";

  private static final String CODE_IS_PRESENT = "NYC_USA";

  @Test
  void testAddShouldReturn400BadRequest() throws Exception {
    LocationRequest request = new LocationRequest();

    String bodyContent = objectMapper.writeValueAsString(request);

    mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
          .andExpect(status().isBadRequest())
          .andDo(print());
  }

  @Test
  void testAddShouldReturn201Created() throws Exception {

    Mockito.when(locationService.create(CREATE_NYC_USA_REQUEST)).thenReturn(NYC_USA_RESPONSE);

    String bodyContent = objectMapper.writeValueAsString(CREATE_NYC_USA_REQUEST);

    mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
          .andExpect(status().isCreated())
          .andExpect(content().contentType("application/json"))
          .andExpect(jsonPath("$.code", CoreMatchers.is("NYC_USA")))
          .andExpect(header().string("Location", "/api/v1/locations/NYC_USA"))
          .andDo(print());

  }

  @Test
  void testListShouldReturn204NoContent() throws Exception {
    Mockito.when(locationService.list()).thenReturn(Collections.emptyList());

    mockMvc.perform(get(END_POINT_PATH))
          .andExpect(status().isNoContent())
          .andDo(print());
  }


  @Test
  void testListShouldReturn200OK() throws Exception {

    Mockito.when(locationService.list()).thenReturn(List.of(NYC_USA_RESPONSE, DELHI_IN_RESPONSE));

    mockMvc.perform(get(END_POINT_PATH))
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/json"))
          .andExpect(jsonPath("$[0].code", CoreMatchers.is("NYC_USA")))
          .andExpect(jsonPath("$[1].code", CoreMatchers.is("DELHI_IN")))
          .andDo(print());
  }

  @Test
  void testGetShouldReturn405MethodNotAllowed() throws Exception {
    String requestURI = END_POINT_PATH + "/" + CODE_IS_NOT_PRESENT;

    mockMvc.perform(post(requestURI))
          .andExpect(status().isMethodNotAllowed())
          .andDo(print());
  }

  @Test
  void testGetShouldReturn404NotFound() throws Exception {

    Mockito.when(locationService.detail(CODE_IS_NOT_PRESENT)).thenThrow(LocationNotFoundException.class);

    mockMvc.perform(get(END_POINT_PATH + CODE_IS_NOT_PRESENT))
          .andExpect(status().isNotFound())
          .andDo(print());
  }

  @Test
  void testGetShouldReturn200OK() throws Exception {
    Mockito.when(locationService.detail(CODE_IS_PRESENT)).thenReturn(NYC_USA_RESPONSE);

    mockMvc.perform(get(END_POINT_PATH + "/" + CODE_IS_PRESENT))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", CoreMatchers.is("NYC_USA")))
          .andExpect(jsonPath("$.city_name", CoreMatchers.is("New York City")))
          .andDo(print());
  }

  @Test
  void testUpdateShouldReturn404NotFound() throws Exception {
    Mockito.when(locationService.update(CODE_IS_NOT_PRESENT, UPDATE_NYC_USA_REQUEST))
          .thenThrow(LocationNotFoundException.class);

    String bodyContent = objectMapper.writeValueAsString(UPDATE_NYC_USA_REQUEST);

    mockMvc.perform(put(END_POINT_PATH + "/" + CODE_IS_NOT_PRESENT)
                .contentType("application/json").content(bodyContent))
          .andExpect(status().isNotFound())
          .andDo(print());
  }

  @Test
  void testUpdateShouldReturn400BadRequest() throws Exception {

    String bodyContent = objectMapper.writeValueAsString(FAKE_REQUEST_INVALID);

    mockMvc.perform(put(END_POINT_PATH + "/" + CODE_IS_NOT_PRESENT)
                .contentType("application/json").content(bodyContent))
          .andExpect(status().isBadRequest())
          .andDo(print());
  }

  @Test
  void testValidateRequestBodyLocationCode() throws Exception {
    String bodyContent = objectMapper.writeValueAsString(FAKE_REQUEST_INVALID);

    MvcResult result = mockMvc.perform(post(END_POINT_PATH)
                .contentType("application/json").content(bodyContent))
          .andExpect(status().isBadRequest())
          .andDo(print())
          .andReturn();

    String response = result.getResponse().getContentAsString();

    assertThat(response, allOf(
          containsString("Code is mandatory"),
          containsString("City name is mandatory")
    ));
  }


  @Test
  void testValidateRequestBodyLocationCodeLength() throws Exception {

    String bodyContent = objectMapper.writeValueAsString(FAKE_REQUEST_INVALID_LENGTH);

    mockMvc.perform(post(END_POINT_PATH)
                .contentType("application/json").content(bodyContent))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.errors[0]", CoreMatchers.is("Code must be between 3 and 12 characters")))
          .andDo(print());
  }

  @Test
  void testValidateRequestBodyAllFieldsInvalid() throws Exception {

    LocationRequest request = FAKE_REQUEST_INVALID;

    request.setRegionName("");
    request.setCountryCode("");
    request.setCountryName("");

    String bodyContent = objectMapper.writeValueAsString(FAKE_REQUEST_INVALID);

    MvcResult mvcResult = mockMvc.perform(post(END_POINT_PATH)
                .contentType("application/json").content(bodyContent))
          .andExpect(status().isBadRequest())
          .andDo(print())
          .andReturn();

    String response = mvcResult.getResponse().getContentAsString();

    assertThat(response).contains("Code is mandatory");
    assertThat(response).contains("City name is mandatory");
    assertThat(response).contains("Region name must be between 3 and 128 characters");
    assertThat(response).contains("Country name must be between 3 and 64 characters");
    assertThat(response).contains("Country code must be 2 characters");
  }

  @Test
  void testUpdateShouldReturn200OK() throws Exception {
    Mockito.when(locationService.update(CODE_IS_PRESENT, UPDATE_NYC_USA_REQUEST)).thenReturn(NYC_USA_RESPONSE);

    String bodyContent = objectMapper.writeValueAsString(UPDATE_NYC_USA_REQUEST);

    mockMvc.perform(put(END_POINT_PATH + "/" + CODE_IS_PRESENT)
                .contentType("application/json").content(bodyContent))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", CoreMatchers.is("NYC_USA")))
          .andExpect(jsonPath("$.city_name", CoreMatchers.is("New York City")))
          .andDo(print());
  }

  @Test
  void testDeleteShouldReturn404NotFound() throws Exception {

    Mockito.doThrow(LocationNotFoundException.class).when(locationService).delete(CODE_IS_NOT_PRESENT);

    String requestURI = END_POINT_PATH + "/" + CODE_IS_NOT_PRESENT;

    mockMvc.perform(delete(requestURI))
          .andExpect(status().isNotFound())
          .andDo(print());
  }


  @Test
  void testDeleteShouldReturn204NoContent() throws Exception {

    Mockito.doNothing().when(locationService).delete(CODE_IS_PRESENT);

    String requestURI = END_POINT_PATH + "/" + CODE_IS_PRESENT;

    mockMvc.perform(delete(requestURI))
          .andExpect(status().isNoContent())
          .andDo(print());
  }

  private static LocationRequest fakeCreatedRequest() {
    return LocationRequest.builder()
          .code("NYC_USA")
          .cityName("New York City")
          .regionName("New York")
          .countryCode("US")
          .countryName("United State of America")
          .build();
  }

  private static LocationUpdateRequest fakeUpdatedRequest() {
    return LocationUpdateRequest.builder()
          .cityName("New York City")
          .regionName("New York")
          .countryCode("US")
          .countryName("United State of America")
          .build();
  }


  private static LocationResponse fakeNycUsa() {
    return LocationResponse.builder()
          .code("NYC_USA")
          .cityName("New York City")
          .regionName("New York")
          .countryCode("US")
          .countryName("United State of America")
          .enabled(true)
          .build();
  }

  private static LocationResponse fakeDelhiIn() {
    return LocationResponse.builder()
          .code("DELHI_IN")
          .cityName("New Delhi")
          .regionName("Delhi")
          .countryCode("IN")
          .countryName("India")
          .enabled(true)
          .build();
  }

  private static LocationRequest fakeRequestInvalid() {
    return LocationRequest.builder()
          .regionName("HaNoi")
          .countryCode("VN")
          .countryName("Viet Nam")
          .enabled(true)
          .build();
  }

  private static LocationRequest fakeRequestInvalidLength() {
    return LocationRequest.builder()
          .code("12345678901234567890")
          .cityName("New York City")
          .regionName("New York")
          .countryCode("US")
          .countryName("United State of America")
          .build();
  }
}
