package com.example.ordersync.dto;

import com.example.ordersync.support.SyncMode;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class OrderSyncRequest {

    @NotNull
    private SyncMode mode = SyncMode.INCREMENTAL;

    private LocalDateTime since;

    public SyncMode getMode() {
        return mode;
    }

    public void setMode(SyncMode mode) {
        this.mode = mode;
    }

    public LocalDateTime getSince() {
        return since;
    }

    public void setSince(LocalDateTime since) {
        this.since = since;
    }
}
