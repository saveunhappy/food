package com.imooc.food.orderservicemanager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.food.orderservicemanager.dao.OrderDetailDao;
import com.imooc.food.orderservicemanager.dto.OrderMessageDTO;
import com.imooc.food.orderservicemanager.enumeration.OrderStatus;
import com.imooc.food.orderservicemanager.po.OrderDetailPO;
import com.imooc.food.orderservicemanager.vo.OrderCreateVO;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

/**
 * 处理用户关于订单的请求
 */
@Service
public class OrderService {
    @Resource
    private OrderDetailDao orderDetailDao;
    @Resource
    private ObjectMapper objectMapper;
    public void createOrder(OrderCreateVO orderCreateVO){
        OrderDetailPO orderDetailPO = new OrderDetailPO();
        orderDetailPO.setAddress(orderCreateVO.getAddress());
        orderDetailPO.setAccountId(orderCreateVO.getAccountId());
        orderDetailPO.setProductId(orderCreateVO.getProductId());
        orderDetailPO.setStatus(OrderStatus.ORDER_CREATING);
        orderDetailPO.setDate(new Date());
        orderDetailDao.insert(orderDetailPO);

        OrderMessageDTO orderMessageDTO = new OrderMessageDTO();
        orderMessageDTO.setOrderId(orderDetailPO.getId());
        orderMessageDTO.setProductId(orderDetailPO.getProductId());
        orderMessageDTO.setAccountId(orderDetailPO.getAccountId());
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try(Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();) {
            String messageToSend = objectMapper.writeValueAsString(orderMessageDTO);
            channel.basicPublish("exchange.order.restaurant",
                    "key.restaurant",null,messageToSend.getBytes());

        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
