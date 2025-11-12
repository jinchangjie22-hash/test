package com.example.ordersync.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ordersync.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {
}
