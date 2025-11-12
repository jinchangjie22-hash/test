package com.example.ordersync.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ordersync.entity.OrderEntity;

public interface OrderSyncService {

    /**
     * 调用开放平台接口同步订单数据。
     *
     * @param startTime 可选的开始时间（ISO-8601 格式），用于增量同步。
     * @param endTime   可选的结束时间（ISO-8601 格式），用于增量同步。
     * @return 本次同步写入或更新的订单数量。
     */
    int syncOrders(String startTime, String endTime);

    /**
     * 从数据库中分页查询订单数据。
     *
     * @param page 页码，从 1 开始。
     * @param size 每页大小。
     * @return 订单分页结果。
     */
    Page<OrderEntity> listOrders(long page, long size);
}
