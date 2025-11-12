package com.example.ordersync.service;

import com.example.ordersync.support.SyncMode;
import com.example.ordersync.support.SyncSummary;
import java.time.LocalDateTime;

public interface OrderSyncService {

    SyncSummary syncOrders(SyncMode mode, LocalDateTime manualSince);
}
