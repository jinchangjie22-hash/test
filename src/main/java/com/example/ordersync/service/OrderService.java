package com.example.ordersync.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ordersync.entity.OrderEntity;
import java.time.LocalDateTime;

public interface OrderService extends IService<OrderEntity> {

    LocalDateTime findLatestUpdatedAt();
}
