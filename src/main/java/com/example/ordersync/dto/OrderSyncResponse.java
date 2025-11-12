package com.example.ordersync.dto;

import java.time.LocalDateTime;

public class OrderSyncResponse {

    private int fetched;
    private int persisted;
    private LocalDateTime since;
    private LocalDateTime lastUpdatedAt;

    public OrderSyncResponse(int fetched, int persisted, LocalDateTime since, LocalDateTime lastUpdatedAt) {
        this.fetched = fetched;
        this.persisted = persisted;
        this.since = since;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public int getFetched() {
        return fetched;
    }

    public void setFetched(int fetched) {
        this.fetched = fetched;
    }

    public int getPersisted() {
        return persisted;
    }

    public void setPersisted(int persisted) {
        this.persisted = persisted;
    }

    public LocalDateTime getSince() {
        return since;
    }

    public void setSince(LocalDateTime since) {
        this.since = since;
    }

    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
}
