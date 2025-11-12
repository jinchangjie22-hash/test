package com.example.ordersync.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ordersync.entity.OrderEntity;
import com.example.ordersync.mapper.OrderMapper;
import com.example.ordersync.service.OrderService;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderEntity> implements OrderService {

    @Override
    public LocalDateTime findLatestUpdatedAt() {
        return getBaseMapper().selectMaxUpdatedAt();
    }
}
