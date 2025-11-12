package com.example.ordersync.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ordersync.config.OpenApiProperties;
import com.example.ordersync.entity.OrderEntity;
import com.example.ordersync.mapper.OrderMapper;
import com.example.ordersync.model.OrderListResponse;
import com.example.ordersync.service.OrderSyncService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrderSyncServiceImpl extends ServiceImpl<OrderMapper, OrderEntity> implements OrderSyncService {

    private static final Logger log = LoggerFactory.getLogger(OrderSyncServiceImpl.class);

    private static final int DEFAULT_PAGE_SIZE = 50;

    private final RestTemplate restTemplate;
    private final OpenApiProperties properties;

    public OrderSyncServiceImpl(RestTemplate restTemplate, OpenApiProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public int syncOrders(String startTime, String endTime) {
        int pageNum = 1;
        int totalSynced = 0;
        boolean hasMore = true;
        while (hasMore) {
            Map<String, Object> requestBody = buildRequestBody(pageNum, DEFAULT_PAGE_SIZE, startTime, endTime);
            OrderListResponse response = doRequest(requestBody);
            if (response == null) {
                break;
            }
            if (!isResponseSuccess(response)) {
                log.warn("同步订单失败，code={}, message={}", response.getCode(), response.getMessage());
                break;
            }
            List<OrderListResponse.Order> orders = response.getData() != null ? response.getData().getOrders() : Collections.emptyList();
            if (orders == null || orders.isEmpty()) {
                break;
            }
            int saved = persistOrders(orders);
            totalSynced += saved;
            Integer pageSize = response.getData().getPageSize();
            Integer total = response.getData().getTotal();
            Integer currentPage = response.getData().getPageNum();
            int size = pageSize != null && pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE;
            int current = currentPage != null && currentPage > 0 ? currentPage : pageNum;
            if (total != null) {
                hasMore = (long) current * size < total;
            } else {
                hasMore = orders.size() == size;
            }
            pageNum = current + 1;
        }
        return totalSynced;
    }

    @Override
    public Page<OrderEntity> listOrders(long page, long size) {
        Page<OrderEntity> pageRequest = Page.of(page, size);
        LambdaQueryWrapper<OrderEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(OrderEntity::getCreatedAt).orderByDesc(OrderEntity::getUpdatedAt);
        return this.page(pageRequest, wrapper);
    }

    private OrderListResponse doRequest(Map<String, Object> body) {
        String url = properties.getBaseUrl() + properties.getOrderListPath();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(body, headers);
        ResponseEntity<OrderListResponse> responseEntity = restTemplate.postForEntity(url, httpEntity, OrderListResponse.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            log.warn("请求订单列表失败，status={}", responseEntity.getStatusCode());
            return null;
        }
        return responseEntity.getBody();
    }

    private Map<String, Object> buildRequestBody(int pageNum, int pageSize, String startTime, String endTime) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("page", pageNum);
        payload.put("page_size", pageSize);
        if (StringUtils.hasText(startTime)) {
            payload.put("start_time", startTime);
        }
        if (StringUtils.hasText(endTime)) {
            payload.put("end_time", endTime);
        }
        Map<String, String> commonParams = properties.getCommonParams();
        if (commonParams != null) {
            payload.putAll(commonParams);
        }
        payload.put("app_key", properties.getAppKey());
        payload.put("timestamp", String.valueOf(System.currentTimeMillis()));
        payload.put("sign", buildSignature(payload));
        return payload;
    }

    private String buildSignature(Map<String, Object> payload) {
        List<String> keyValues = payload.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !Objects.equals(entry.getKey(), "sign"))
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.toList());
        String signContent = properties.getAppSecret() + String.join("&", keyValues) + properties.getAppSecret();
        return DigestUtils.md5Hex(signContent).toUpperCase();
    }

    private int persistOrders(List<OrderListResponse.Order> orders) {
        LocalDateTime now = LocalDateTime.now();
        List<OrderEntity> entities = new ArrayList<>(orders.size());
        for (OrderListResponse.Order order : orders) {
            OrderEntity entity = new OrderEntity();
            String orderId = StringUtils.hasText(order.getId()) ? order.getId() : order.getOrderNo();
            entity.setOrderId(orderId);
            entity.setOrderNo(order.getOrderNo());
            entity.setStatus(order.getStatus());
            entity.setAmount(order.getAmount());
            entity.setBuyerName(order.getBuyerName());
            entity.setBuyerPhone(order.getBuyerPhone());
            entity.setPayTime(convert(order.getPayTime()));
            entity.setCreatedAt(convert(order.getCreatedAt()));
            entity.setUpdatedAt(convert(order.getUpdatedAt()));
            entity.setSyncTime(now);
            entities.add(entity);
        }
        if (entities.isEmpty()) {
            return 0;
        }
        saveOrUpdateBatch(entities, 100);
        return entities.size();
    }

    private boolean isResponseSuccess(OrderListResponse response) {
        if (response == null) {
            return false;
        }
        if (response.isSuccess()) {
            return true;
        }
        String code = response.getCode();
        return "0".equals(code) || "200".equals(code) || "SUCCESS".equalsIgnoreCase(code);
    }

    private LocalDateTime convert(OffsetDateTime time) {
        if (time == null) {
            return null;
        }
        return time.withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }
}
