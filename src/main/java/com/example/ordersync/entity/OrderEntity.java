package com.example.ordersync.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("orders")
public class OrderEntity {

    @TableId(value = "order_id", type = IdType.INPUT)
    private String orderId;

    @TableField("order_no")
    private String orderNo;

    @TableField("status")
    private String status;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("currency")
    private String currency;

    @TableField("buyer_name")
    private String buyerName;

    @TableField("receiver_name")
    private String receiverName;

    @TableField("receiver_phone")
    private String receiverPhone;

    @TableField("order_time")
    private LocalDateTime orderTime;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableField("raw_payload")
    private String rawPayload;
}
