package com.bikes.renting.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class RentalControllerTest {
    private String baseUrl = "http://localhost:8080";

    @Test
    public void getWithoutParameters_then400IsReceived() throws IOException {
        // Given
        final HttpUriRequest request = new HttpGet(baseUrl + "/bike/rental");

        // When
        final HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
    }

    @Test
    public void postWithoutParameters_then400IsReceived() throws IOException {
        // Given
        final HttpUriRequest request = new HttpPost(baseUrl + "/bike/rental");

        // When
        final HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
    }

    @Test
    public void getWithCorrectParameters_then200IsReceived() throws IOException {
        // Given
        final HttpUriRequest request = new HttpGet(baseUrl + "/bike/rental?rentalType=family&subRental=day&quantity=1");

        // When
        final HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }

    @Test
    public void postWithCorrectParameters_then200IsReceived() throws IOException {
        // Given
        final HttpUriRequest request = new HttpPost(baseUrl + "/bike/rental?rentalType=family&subRental=day&quantity=1");

        // When
        final HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }

    @Test
    public void getWithCorrectParameters_then200IsReceived2() throws IOException {
        // Given
        final HttpUriRequest request = new HttpGet(baseUrl + "/bike/rental?rentalType=day&quantity=1");

        // When
        final HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }

    @Test
    public void postWithCorrectParameters_then200IsReceived2() throws IOException {
        // Given
        final HttpUriRequest request = new HttpPost(baseUrl + "/bike/rental?rentalType=day&quantity=1");

        // When
        final HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }
}
