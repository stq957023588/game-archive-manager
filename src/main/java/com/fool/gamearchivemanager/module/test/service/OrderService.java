package com.fool.gamearchivemanager.module.test.service;

import com.fool.gamearchivemanager.config.cache.CacheTemplate;
import com.fool.gamearchivemanager.entity.enums.OrderStatus;
import com.fool.gamearchivemanager.entity.enums.OrderStatusChangeEvent;
import com.fool.gamearchivemanager.entity.po.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@Service
public class OrderService {

    private final StateMachine<OrderStatus, OrderStatusChangeEvent> orderStateMachine;

    private final StateMachinePersister<OrderStatus, OrderStatusChangeEvent, Order> orderStateMachinePersister;


    private final AtomicInteger idInc;

    private final Map<Integer, Order> orders;

    private final CacheTemplate cacheTemplate;

    public OrderService(StateMachine<OrderStatus, OrderStatusChangeEvent> orderStateMachine, StateMachinePersister<OrderStatus, OrderStatusChangeEvent, Order> orderStateMachinePersister, CacheTemplate cacheTemplate) {
        this.orderStateMachine = orderStateMachine;
        this.orderStateMachinePersister = orderStateMachinePersister;
        this.cacheTemplate = cacheTemplate;
        this.idInc = new AtomicInteger();
        orders = new HashMap<>();
    }


    public Order create() {
        Order order = new Order();
        order.setId(idInc.getAndIncrement());
        order.setStatus(OrderStatus.WAIT_PAYMENT);

        cacheTemplate.put(String.valueOf(order.getId()), order);

        return order;
    }


    public Order pay(int orderId) {
        // Order order = orders.get(orderId);
        Order order = (Order) cacheTemplate.get(String.valueOf(orderId));
        log.info("尝试支付，订单号：{}", orderId);
        sendEvent(OrderStatusChangeEvent.PAY, order, () -> {
            log.info("Pay succeed");
        });
        return order;
    }


    public Order deliver(int id) {
        Order order = (Order) cacheTemplate.get(String.valueOf(id));
        log.info("尝试发货，订单号：{}", id);
        sendEvent(OrderStatusChangeEvent.DELIVERY, order, () -> {
            log.info("Deliver succeed");
        });
        return orders.get(id);
    }

    public Order receive(int id) {
        // Order order = orders.get(id);
        Order order = (Order) cacheTemplate.get(String.valueOf(id));
        log.info("尝试收货，订单号：{}", id);
        sendEvent(OrderStatusChangeEvent.RECEIVE, order, () -> {
            log.info("Receive succeed");
        });
        return orders.get(id);
    }


    private synchronized void sendEvent(OrderStatusChangeEvent event, Order order, Runnable successHandler) {
        try {
            Thread.sleep(1000L);
            // 根据上下文对象从 缓存/存储 中读取流转的对象到内存中
            orderStateMachinePersister.restore(orderStateMachine, order);
            Message<OrderStatusChangeEvent> message = MessageBuilder.withPayload(event).setHeader("order", order).build();
            Mono<Message<OrderStatusChangeEvent>> mono = Mono.just(message);
            orderStateMachine.sendEvent(mono).doOnComplete(() -> {
                        if (successHandler != null) {
                            successHandler.run();
                        }
                        try {
                            orderStateMachinePersister.persist(orderStateMachine, order);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }).doOnError(error -> {
                        log.info("", error);
                    }).doOnCancel(() -> {
                        log.info("Canceled");
                    })
                    .subscribe();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
