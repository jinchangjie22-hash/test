package com.example.ordersync.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ordersync.entity.OrderEntity;
import com.example.ordersync.service.OrderSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderSyncService orderSyncService;

    public OrderController(OrderSyncService orderSyncService) {
        this.orderSyncService = orderSyncService;
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncOrders(@RequestBody(required = false) SyncRequest request) {
        String startTime = request != null ? request.getStartTime() : null;
        String endTime = request != null ? request.getEndTime() : null;
        int count = orderSyncService.syncOrders(startTime, endTime);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("synced", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<OrderEntity>> listOrders(@RequestParam(defaultValue = "1") long page,
                                                        @RequestParam(defaultValue = "10") long size) {
        return ResponseEntity.ok(orderSyncService.listOrders(page, size));
    }

    public static class SyncRequest {
        private String startTime;
        private String endTime;

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
    }
}
