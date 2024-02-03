package com.skyapi.weather.service.repository;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class IP2LocationTests {

  String DBPath = "ip2locdb/IP2LOCATION-LITE-DB3.BIN";

  @Test
  void testInvalidIP() throws IOException {
    IP2Location ip2Location = new IP2Location();
    ip2Location.Open(DBPath);

    String ipAddress = "abc";
    IPResult ipResult = ip2Location.IPQuery(ipAddress);

    System.out.println(ipResult);

    assertThat(ipResult.getStatus()).isEqualTo("INVALID_IP_ADDRESS");

  }

  @Test
  void testValidIP() throws IOException {
    IP2Location ip2Location = new IP2Location();
    ip2Location.Open(DBPath);

    String ipAddress = " 171.236.56.131";
    IPResult ipResult = ip2Location.IPQuery(ipAddress);

    System.out.println(ipResult);

    assertThat(ipResult.getStatus()).isEqualTo("OK");

    assertThat(ipResult.getCountryLong()).isEqualTo("Viet Nam");
    assertThat(ipResult.getRegion()).isEqualTo("Vinh Phuc");
    assertThat(ipResult.getCity()).isEqualTo("Vinh Yen");

  }

}
