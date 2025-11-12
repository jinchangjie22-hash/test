package com.example.ordersync.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "ordersync.openapi")
public class OpenApiProperties {

    /**
     * 基础服务地址，例如：https://openapi.example.com
     */
    private String baseUrl;

    /**
     * 订单查询接口路径。
     */
    private String orderListPath;

    /**
     * 应用的 appKey。
     */
    private String appKey;

    /**
     * 应用的 appSecret。
     */
    private String appSecret;

    /**
     * 额外的公共请求参数。
     */
    private Map<String, String> commonParams;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getOrderListPath() {
        return orderListPath;
    }

    public void setOrderListPath(String orderListPath) {
        this.orderListPath = orderListPath;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public Map<String, String> getCommonParams() {
        return commonParams;
    }

    public void setCommonParams(Map<String, String> commonParams) {
        this.commonParams = commonParams;
    }
}
