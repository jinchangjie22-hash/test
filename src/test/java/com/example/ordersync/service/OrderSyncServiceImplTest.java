package com.example.ordersync.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.ordersync.client.OpenApiClient;
import com.example.ordersync.client.dto.OpenApiOrderResponse;
import com.example.ordersync.config.OpenApiProperties;
import com.example.ordersync.entity.OrderEntity;
import com.example.ordersync.service.impl.OrderSyncServiceImpl;
import com.example.ordersync.support.SyncMode;
import com.example.ordersync.support.SyncSummary;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderSyncServiceImplTest {

    @Mock
    private OrderService orderService;

    @Mock
    private OpenApiClient openApiClient;

    private OrderSyncServiceImpl orderSyncService;

    @Captor
    private ArgumentCaptor<List<OrderEntity>> entityCaptor;

    @BeforeEach
    void setUp() {
        OpenApiProperties properties = new OpenApiProperties();
        properties.setBaseUrl("https://example.com");
        properties.setListPath("/orders");
        properties.setClientId("id");
        properties.setClientSecret("secret");
        properties.setPageSize(50);
        orderSyncService = new OrderSyncServiceImpl(orderService, openApiClient, properties);
    }

    @Test
    void syncOrdersIncrementalUsesLatestTimestamp() {
        LocalDateTime latest = LocalDateTime.of(2024, 1, 1, 12, 0);
        when(orderService.findLatestUpdatedAt()).thenReturn(latest);

        OpenApiOrderResponse response = buildResponse(List.of(buildOrder("1"), buildOrder("2")), 1, 50, 2);
        when(openApiClient.fetchOrders(1, 50, latest)).thenReturn(response);

        SyncSummary summary = orderSyncService.syncOrders(SyncMode.INCREMENTAL, null);

        assertThat(summary.getSince()).isEqualTo(latest);
        assertThat(summary.getFetchedCount()).isEqualTo(2);
        assertThat(summary.getPersistedCount()).isEqualTo(2);
        assertThat(summary.getLastUpdatedAt()).isNotNull();

        verify(orderService).saveOrUpdateBatch(entityCaptor.capture());
        assertThat(entityCaptor.getValue())
                .hasSize(2)
                .allSatisfy(entity -> assertThat(entity.getOrderId()).isNotBlank());
        verify(orderService).findLatestUpdatedAt();
        verifyNoMoreInteractions(orderService);
    }

    @Test
    void syncOrdersFullIgnoresLatestTimestamp() {
        OpenApiOrderResponse response = buildResponse(List.of(buildOrder("10")), 1, 50, 1);
        when(openApiClient.fetchOrders(1, 50, null)).thenReturn(response);

        SyncSummary summary = orderSyncService.syncOrders(SyncMode.FULL, null);

        assertThat(summary.getSince()).isNull();
        assertThat(summary.getFetchedCount()).isEqualTo(1);
        assertThat(summary.getPersistedCount()).isEqualTo(1);
        verify(orderService).saveOrUpdateBatch(anyCollection());
        verifyNoMoreInteractions(orderService);
    }

    @Test
    void syncOrdersManualSinceOverridesMode() {
        LocalDateTime since = LocalDateTime.of(2023, 12, 1, 0, 0);
        OpenApiOrderResponse response = buildResponse(List.of(buildOrder("11")), 1, 50, 1);
        when(openApiClient.fetchOrders(1, 50, since)).thenReturn(response);

        SyncSummary summary = orderSyncService.syncOrders(SyncMode.INCREMENTAL, since);

        assertThat(summary.getSince()).isEqualTo(since);
        verify(orderService).saveOrUpdateBatch(anyCollection());
        verifyNoMoreInteractions(orderService);
    }

    private OpenApiOrderResponse buildResponse(List<OpenApiOrderResponse.Order> orders, int page, int size, long total) {
        OpenApiOrderResponse response = new OpenApiOrderResponse();
        response.setCode(0);
        OpenApiOrderResponse.Data data = new OpenApiOrderResponse.Data();
        data.setOrders(orders);
        OpenApiOrderResponse.Pagination pagination = new OpenApiOrderResponse.Pagination();
        pagination.setPage(page);
        pagination.setPageSize(size);
        pagination.setTotal(total);
        data.setPagination(pagination);
        response.setData(data);
        return response;
    }

    private OpenApiOrderResponse.Order buildOrder(String id) {
        OpenApiOrderResponse.Order order = new OpenApiOrderResponse.Order();
        order.setOrderId(id);
        order.setOrderNo("NO" + id);
        order.setStatus("PAID");
        order.setTotalAmount(BigDecimal.valueOf(100));
        order.setCurrency("CNY");
        order.setBuyerName("buyer" + id);
        order.setReceiverName("receiver" + id);
        order.setReceiverPhone("13000000000");
        order.setOrderTime(LocalDateTime.now().minusDays(1));
        order.setUpdatedAt(LocalDateTime.now());
        order.setRawPayload("{}");
        return order;
    }
}
