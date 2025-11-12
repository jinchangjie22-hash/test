# Order Sync Service

基于 Spring Boot + MyBatis-Plus 的订单同步示例项目，演示如何调用开放平台接口同步订单信息并存储到 MySQL 数据库，同时提供查询接口读取同步后的数据。

## 功能概述

- **订单同步接口**：`POST /orders/sync`
  - 可选参数 `startTime`、`endTime`（ISO-8601 字符串），用于增量同步。
  - 调用开放平台订单列表接口，按页抓取数据并落库，若订单已存在则执行更新。
- **订单查询接口**：`GET /orders`
  - 支持分页参数 `page`、`size`。
  - 直接从本地数据库读取同步后的订单数据。

## 同步策略

- 默认按分页全量拉取（每页 50 条），直到接口返回的数据不足一页或达到总数。
- 每次同步前会根据请求参数生成签名，确保与开放平台签名规范兼容。
- 保存数据时使用 MyBatis-Plus `saveOrUpdateBatch`，以订单 ID 或订单号作为主键，实现新增、更新合并。
- 记录最近一次同步时间（`sync_time` 字段），便于后续排查。

## 快速开始

1. 准备 MySQL 数据库并创建表：

   ```sql
   CREATE TABLE `t_order` (
     `order_id` varchar(64) NOT NULL,
     `order_no` varchar(64) DEFAULT NULL,
     `status` varchar(32) DEFAULT NULL,
     `amount` decimal(18,2) DEFAULT NULL,
     `buyer_name` varchar(128) DEFAULT NULL,
     `buyer_phone` varchar(32) DEFAULT NULL,
     `pay_time` datetime DEFAULT NULL,
     `created_at` datetime DEFAULT NULL,
     `updated_at` datetime DEFAULT NULL,
     `sync_time` datetime DEFAULT NULL,
     PRIMARY KEY (`order_id`)
   ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
   ```

2. 根据开放平台接口文档配置 `src/main/resources/application.yml` 中的 `ordersync.openapi` 信息（`base-url`、`order-list-path`、`app-key`、`app-secret` 等）。

3. 启动项目：

   ```bash
   mvn spring-boot:run
   ```

4. 调用同步接口触发同步，再使用查询接口查看结果。

## 参考

- [开放平台接入说明](https://s.apifox.cn/3ac13d69-5a38-4536-ae9b-a54001854ef8/doc-2686716)
- [开放平台接口示例](https://s.apifox.cn/3ac13d69-5a38-4536-ae9b-a54001854ef8/doc-2686717)
