package com.imooc.food.orderservicemanager.service;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息处理相关的业务逻辑
 */
@Service
public class OrderMessageService {
    /**
     * 声明消息队列、交换机、绑定、消息的处理
     */
    public static void handleMessage() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
//        connectionFactory.setPort(15672);
        try(Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel()){
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
        }
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        handleMessage();
    }
}
