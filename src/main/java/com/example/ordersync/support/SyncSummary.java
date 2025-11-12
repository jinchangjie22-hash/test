package com.example.ordersync.support;

import java.time.LocalDateTime;

public class SyncSummary {

    private final int fetchedCount;
    private final int persistedCount;
    private final LocalDateTime since;
    private final LocalDateTime lastUpdatedAt;

    public SyncSummary(int fetchedCount, int persistedCount, LocalDateTime since, LocalDateTime lastUpdatedAt) {
        this.fetchedCount = fetchedCount;
        this.persistedCount = persistedCount;
        this.since = since;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public int getFetchedCount() {
        return fetchedCount;
    }

    public int getPersistedCount() {
        return persistedCount;
    }

    public LocalDateTime getSince() {
        return since;
    }

    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }
}
