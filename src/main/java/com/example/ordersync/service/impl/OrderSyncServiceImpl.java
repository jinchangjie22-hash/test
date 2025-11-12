package com.example.ordersync.service.impl;

import com.example.ordersync.client.OpenApiClient;
import com.example.ordersync.client.dto.OpenApiOrderResponse;
import com.example.ordersync.config.OpenApiProperties;
import com.example.ordersync.entity.OrderEntity;
import com.example.ordersync.service.OrderService;
import com.example.ordersync.service.OrderSyncService;
import com.example.ordersync.support.SyncMode;
import com.example.ordersync.support.SyncSummary;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderSyncServiceImpl implements OrderSyncService {

    private static final Logger log = LoggerFactory.getLogger(OrderSyncServiceImpl.class);

    private final OrderService orderService;
    private final OpenApiClient openApiClient;
    private final OpenApiProperties properties;

    public OrderSyncServiceImpl(OrderService orderService, OpenApiClient openApiClient, OpenApiProperties properties) {
        this.orderService = orderService;
        this.openApiClient = openApiClient;
        this.properties = properties;
    }

    @Override
    @Transactional
    public SyncSummary syncOrders(SyncMode mode, LocalDateTime manualSince) {
        LocalDateTime since = resolveSince(mode, manualSince);

        int page = 1;
        int fetched = 0;
        int persisted = 0;
        LocalDateTime lastUpdated = null;

        while (true) {
            OpenApiOrderResponse response = openApiClient.fetchOrders(page, properties.getPageSize(), since);
            if (response == null) {
                log.warn("Open API returned null response at page {}", page);
                break;
            }
            if (!response.isSuccess()) {
                throw new IllegalStateException("Open API error: " + response.getMessage());
            }
            List<OrderEntity> current = response.getOrders().stream()
                    .map(this::mapToEntity)
                    .filter(Objects::nonNull)
                    .toList();
            fetched += current.size();

            if (!current.isEmpty()) {
                orderService.saveOrUpdateBatch(current);
                persisted += current.size();
                LocalDateTime pageMaxUpdated = current.stream()
                        .map(OrderEntity::getUpdatedAt)
                        .filter(Objects::nonNull)
                        .max(Comparator.naturalOrder())
                        .orElse(null);
                if (pageMaxUpdated != null) {
                    lastUpdated = lastUpdated == null ? pageMaxUpdated
                            : lastUpdated.isAfter(pageMaxUpdated) ? lastUpdated : pageMaxUpdated;
                }
            }

            if (!response.hasNextPage() || response.getOrders().isEmpty()) {
                break;
            }
            page++;
        }

        return new SyncSummary(fetched, persisted, since, lastUpdated);
    }

    private LocalDateTime resolveSince(SyncMode mode, LocalDateTime manualSince) {
        if (manualSince != null) {
            return manualSince;
        }
        if (mode == SyncMode.FULL) {
            return null;
        }
        return orderService.findLatestUpdatedAt();
    }

    private OrderEntity mapToEntity(OpenApiOrderResponse.Order order) {
        if (order == null || order.getOrderId() == null) {
            return null;
        }
        OrderEntity entity = new OrderEntity();
        entity.setOrderId(order.getOrderId());
        entity.setOrderNo(order.getOrderNo());
        entity.setStatus(order.getStatus());
        entity.setTotalAmount(order.getTotalAmount());
        entity.setCurrency(order.getCurrency());
        entity.setBuyerName(order.getBuyerName());
        entity.setReceiverName(order.getReceiverName());
        entity.setReceiverPhone(order.getReceiverPhone());
        entity.setOrderTime(order.getOrderTime());
        entity.setUpdatedAt(order.getUpdatedAt());
        entity.setRawPayload(order.getRawPayload());
        return entity;
    }
}
