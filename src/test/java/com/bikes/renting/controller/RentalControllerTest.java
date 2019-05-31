package com.bikes.renting.controller;

import com.bikes.renting.SimpleKafkaProducerApplication;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SimpleKafkaProducerApplication.class, webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RentalControllerTest {
    private String baseUrl = "http://127.0.0.1:";

    @LocalServerPort
    int randomServerPort;

    @Before
    public void setup(){
        baseUrl = baseUrl + randomServerPort;
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
    public void postWithCorrectParameters_then200IsReceived() throws IOException {
        // Given
        final HttpUriRequest request = new HttpPost(baseUrl + "/bike/rental?rentalType=family&subRental=day&subRental=week&subRental=hour&quantity=2&quantity=1&quantity=3");
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

    @Test
    public void postWithExcessOfParameters_then500IsReceived() throws IOException {
        // Given
        final HttpUriRequest request = new HttpPost(baseUrl + "/bike/rental?rentalType=family&subRental=day&subRental=week&subRental=hour&subRental=hour&subRental=hour&subRental=hour&quantity=2&quantity=1&quantity=3&quantity=3&quantity=3&quantity=3");

        // When
        final HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_INTERNAL_SERVER_ERROR));
    }
}
