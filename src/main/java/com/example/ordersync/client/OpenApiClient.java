package com.example.ordersync.client;

import com.example.ordersync.client.dto.OpenApiOrderResponse;
import java.time.LocalDateTime;

public interface OpenApiClient {

    OpenApiOrderResponse fetchOrders(int page, int pageSize, LocalDateTime updatedAfter);
}
