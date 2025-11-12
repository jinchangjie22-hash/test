package com.example.ordersync;

import com.example.ordersync.config.OpenApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(OpenApiProperties.class)
public class OrderSyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderSyncApplication.class, args);
    }
}
