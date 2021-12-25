package com.imooc.food.orderservicemanager.config;

import com.imooc.food.orderservicemanager.service.OrderMessageService;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Configuration
public class RabbitConfig {
    @Resource
    OrderMessageService orderMessageService;
    public void startListenMessage() throws InterruptedException, TimeoutException, IOException {
        orderMessageService.handleMessage();
    }
}
