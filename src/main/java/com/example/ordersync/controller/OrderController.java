package com.example.ordersync.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ordersync.dto.OrderPageResponse;
import com.example.ordersync.dto.OrderQueryRequest;
import com.example.ordersync.dto.OrderSyncRequest;
import com.example.ordersync.dto.OrderSyncResponse;
import com.example.ordersync.entity.OrderEntity;
import com.example.ordersync.service.OrderService;
import com.example.ordersync.service.OrderSyncService;
import com.example.ordersync.support.SyncSummary;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@Validated
public class OrderController {

    private final OrderService orderService;
    private final OrderSyncService orderSyncService;

    public OrderController(OrderService orderService, OrderSyncService orderSyncService) {
        this.orderService = orderService;
        this.orderSyncService = orderSyncService;
    }

    @PostMapping("/sync")
    public ResponseEntity<OrderSyncResponse> sync(@Valid @RequestBody OrderSyncRequest request) {
        SyncSummary summary = orderSyncService.syncOrders(request.getMode(), request.getSince());
        OrderSyncResponse response = new OrderSyncResponse(summary.getFetchedCount(), summary.getPersistedCount(),
                summary.getSince(), summary.getLastUpdatedAt());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<OrderPageResponse> list(@Valid OrderQueryRequest request) {
        LambdaQueryWrapper<OrderEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(request.getOrderNo())) {
            wrapper.eq(OrderEntity::getOrderNo, request.getOrderNo());
        }
        if (StringUtils.hasText(request.getStatus())) {
            wrapper.eq(OrderEntity::getStatus, request.getStatus());
        }
        if (request.getUpdatedAfter() != null) {
            wrapper.ge(OrderEntity::getUpdatedAt, request.getUpdatedAfter());
        }
        if (request.getUpdatedBefore() != null) {
            wrapper.le(OrderEntity::getUpdatedAt, request.getUpdatedBefore());
        }

        Page<OrderEntity> page = orderService.page(new Page<>(request.getPage(), request.getSize()), wrapper);
        OrderPageResponse response = new OrderPageResponse(page.getTotal(), page.getPages(), page.getRecords());
        return ResponseEntity.ok(response);
    }
}
