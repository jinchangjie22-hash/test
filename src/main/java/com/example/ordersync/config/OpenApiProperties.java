package com.example.ordersync.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "openapi.order")
public class OpenApiProperties {

    /**
     * Base url of the open order API, e.g. https://example.com/openapi.
     */
    @NotBlank
    private String baseUrl;

    /**
     * Concrete path for pulling order list.
     */
    @NotBlank
    private String listPath;

    /**
     * Client/app key issued by the Open API provider.
     */
    @NotBlank
    private String clientId;

    /**
     * Client/app secret issued by the Open API provider.
     */
    @NotBlank
    private String clientSecret;

    /**
     * Default page size for fetching orders.
     */
    @Min(1)
    private int pageSize = 50;

    /**
     * Connection timeout when calling the Open API.
     */
    @NotNull
    private Duration connectTimeout = Duration.ofSeconds(5);

    /**
     * Read timeout when calling the Open API.
     */
    @NotNull
    private Duration readTimeout = Duration.ofSeconds(10);

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getListPath() {
        return listPath;
    }

    public void setListPath(String listPath) {
        this.listPath = listPath;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }
}
