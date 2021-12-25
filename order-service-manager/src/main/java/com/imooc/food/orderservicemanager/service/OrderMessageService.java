package com.imooc.food.orderservicemanager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.food.orderservicemanager.dao.OrderDetailDao;
import com.imooc.food.orderservicemanager.dto.OrderMessageDTO;
import com.imooc.food.orderservicemanager.enumeration.OrderStatus;
import com.imooc.food.orderservicemanager.po.OrderDetailPO;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息处理相关的业务逻辑
 */
@Service
@Slf4j
public class OrderMessageService {
    ObjectMapper objectMapper = new ObjectMapper();
    @Resource
    private OrderDetailDao orderDetailDao;
    /**
     * 声明消息队列、交换机、绑定、消息的处理
     */
    @Async
    public void handleMessage() throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
//        connectionFactory.setPort(15672);
        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare("exchange.order.restaurant",
                    BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null);
            channel.queueDeclare(
                    "queue.order",
                    true,
                    false,
                    false,
                    null
            );
            channel.queueBind("queue.order",
                    "exchange.order.restaurant",
                    "key.order");

            channel.exchangeDeclare("exchange.order.delivery",
                    BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null);

            channel.queueBind("queue.order",
                    "exchange.order.delivery",
                    "key.order");
            channel.basicConsume("queue.order",true,deliverCallback,consumerTag -> {});
            while (true){
                Thread.sleep(10000000);
            }
        }
    }

    DeliverCallback deliverCallback = (consumerTag, message) -> {
        String messageBody = new String(message.getBody());
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try {
            //将消息体反序列化成DTO
            OrderMessageDTO orderMessageDTO = objectMapper.readValue(messageBody, OrderMessageDTO.class);
            //从数据库读取订单
            OrderDetailPO orderDetailPO = orderDetailDao.selectOrder(orderMessageDTO.getOrderId());
            switch (orderDetailPO.getStatus()){
                case ORDER_CREATING:
                    if(orderMessageDTO.getConfirmed() && null != orderMessageDTO.getPrice()){
                        orderDetailPO.setStatus(OrderStatus.RESTAURANT_CONFIRMED);
                        orderDetailPO.setPrice(orderMessageDTO.getPrice());
                        orderDetailDao.update(orderDetailPO);
                        try(Connection connection = connectionFactory.newConnection();
                        Channel channel = connection.createChannel()){
                            String messageToSend = objectMapper.writeValueAsString(orderMessageDTO);
                            channel.basicPublish("exchange.order.deliveryman",
                                    "key.delivery",
                                    null,
                                    messageToSend.getBytes());
                        }
                    }else{
                        orderDetailPO.setStatus(OrderStatus.FAILED);
                        orderDetailDao.update(orderDetailPO);
                    }
                    break;
                case RESTAURANT_CONFIRMED:
                    break;
                case DELIVERTMAN_CONFIRMED:
                    break;
                case SETTLEMENT_CONFIRMED:
                    break;
                case ORDER_CREATE:
                    break;
            }

        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    };


}
