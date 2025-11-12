package com.example.ordersync.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ordersync.entity.OrderEntity;
import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {

    @Select("SELECT MAX(updated_at) FROM orders")
    LocalDateTime selectMaxUpdatedAt();
}
