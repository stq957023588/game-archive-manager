package com.fool.gamearchivemanager.entity.po;

import com.fool.gamearchivemanager.entity.enums.OrderStatus;
import lombok.Data;

@Data
public class Order {

    private int id;

    private OrderStatus status;

}
