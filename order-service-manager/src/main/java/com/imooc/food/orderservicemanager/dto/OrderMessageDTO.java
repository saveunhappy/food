package com.imooc.food.orderservicemanager.dto;

import com.imooc.food.orderservicemanager.enumeration.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
@Getter
@Setter
@ToString
public class OrderMessageDTO {
    private Integer orderId;
    private OrderStatus orderStatus;
    private BigDecimal price;
    private Integer deliverymanId;
    private Integer productId;
    private Integer accountId;
    //结算id
    private Integer settlementId;
    //积分结算Id
    private Integer rewardId;
    //积分奖励数量
    private Integer rewardAmount;
    //确认
    private Boolean confirmed;
}
