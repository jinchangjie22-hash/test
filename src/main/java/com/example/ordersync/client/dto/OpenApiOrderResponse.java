package com.example.ordersync.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenApiOrderResponse {

    private int code;
    private String message;
    private Data data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return code == 0;
    }

    public List<Order> getOrders() {
        return data == null ? Collections.emptyList() : data.getOrders();
    }

    public boolean hasNextPage() {
        if (data == null || data.getPagination() == null) {
            return false;
        }
        Pagination pagination = data.getPagination();
        return pagination.getPage() * pagination.getPageSize() < pagination.getTotal();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {

        @JsonProperty("list")
        private List<Order> orders;

        @JsonProperty("pagination")
        private Pagination pagination;

        public List<Order> getOrders() {
            return orders == null ? Collections.emptyList() : orders;
        }

        public void setOrders(List<Order> orders) {
            this.orders = orders;
        }

        public Pagination getPagination() {
            return pagination;
        }

        public void setPagination(Pagination pagination) {
            this.pagination = pagination;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Pagination {

        @JsonProperty("page")
        private int page;

        @JsonProperty("pageSize")
        private int pageSize;

        @JsonProperty("total")
        private long total;

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Order {

        @JsonProperty("orderId")
        private String orderId;

        @JsonProperty("orderNo")
        private String orderNo;

        @JsonProperty("status")
        private String status;

        @JsonProperty("totalAmount")
        private BigDecimal totalAmount;

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("buyerName")
        private String buyerName;

        @JsonProperty("receiverName")
        private String receiverName;

        @JsonProperty("receiverPhone")
        private String receiverPhone;

        @JsonProperty("orderTime")
        private LocalDateTime orderTime;

        @JsonProperty("updatedAt")
        private LocalDateTime updatedAt;

        @JsonProperty("rawPayload")
        private String rawPayload;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getBuyerName() {
            return buyerName;
        }

        public void setBuyerName(String buyerName) {
            this.buyerName = buyerName;
        }

        public String getReceiverName() {
            return receiverName;
        }

        public void setReceiverName(String receiverName) {
            this.receiverName = receiverName;
        }

        public String getReceiverPhone() {
            return receiverPhone;
        }

        public void setReceiverPhone(String receiverPhone) {
            this.receiverPhone = receiverPhone;
        }

        public LocalDateTime getOrderTime() {
            return orderTime;
        }

        public void setOrderTime(LocalDateTime orderTime) {
            this.orderTime = orderTime;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getRawPayload() {
            return rawPayload;
        }

        public void setRawPayload(String rawPayload) {
            this.rawPayload = rawPayload;
        }
    }
}
