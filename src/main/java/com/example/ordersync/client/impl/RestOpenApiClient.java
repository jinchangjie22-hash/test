package com.example.ordersync.client.impl;

import com.example.ordersync.client.OpenApiClient;
import com.example.ordersync.client.dto.OpenApiOrderResponse;
import com.example.ordersync.config.OpenApiProperties;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class RestOpenApiClient implements OpenApiClient {

    private static final Logger log = LoggerFactory.getLogger(RestOpenApiClient.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestTemplate restTemplate;
    private final OpenApiProperties properties;

    public RestOpenApiClient(RestTemplate restTemplate, OpenApiProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public OpenApiOrderResponse fetchOrders(int page, int pageSize, LocalDateTime updatedAfter) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                .path(properties.getListPath())
                .queryParam("pageNum", page)
                .queryParam("pageSize", pageSize);
        if (updatedAfter != null) {
            builder.queryParam("updatedAfter", FORMATTER.format(updatedAfter));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Client-Id", properties.getClientId());
        headers.set("X-Client-Secret", properties.getClientSecret());

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<OpenApiOrderResponse> response = restTemplate.exchange(
                    builder.build(true).toUri(),
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<>() {
                    });
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Failed to call open order API", e);
            throw e;
        }
    }
}
