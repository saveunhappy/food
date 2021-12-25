package com.imooc.food.orderservicemanager.controller;

import com.imooc.food.orderservicemanager.service.OrderService;
import com.imooc.food.orderservicemanager.vo.OrderCreateVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
@Slf4j
@RestController
public class OrderController {
    @Resource
    OrderService orderService;
    @PostMapping("/orders")
    public void createOrder(@RequestBody OrderCreateVO orderCreateVO){
        log.info("createOrder:orderCreateVo:{}",orderCreateVO);
        orderService.createOrder(orderCreateVO);
    }
}
