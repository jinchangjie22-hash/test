package com.example.ordersync.dto;

import com.example.ordersync.entity.OrderEntity;
import java.util.List;

public class OrderPageResponse {

    private long total;
    private long pages;
    private List<OrderEntity> records;

    public OrderPageResponse(long total, long pages, List<OrderEntity> records) {
        this.total = total;
        this.pages = pages;
        this.records = records;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    public List<OrderEntity> getRecords() {
        return records;
    }

    public void setRecords(List<OrderEntity> records) {
        this.records = records;
    }
}
