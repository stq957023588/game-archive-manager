package com.fool.gamearchivemanager.config.statemachine;


import com.fool.gamearchivemanager.config.cache.CacheTemplate;
import com.fool.gamearchivemanager.entity.enums.OrderStatus;
import com.fool.gamearchivemanager.entity.enums.OrderStatusChangeEvent;
import com.fool.gamearchivemanager.entity.po.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.ensemble.StateMachineEnsemble;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.support.DefaultStateMachineContext;

import java.util.EnumSet;

@Configuration
@EnableStateMachine(name = "orderStateMachine")
public class OrderStateMachineConfiguration extends StateMachineConfigurerAdapter<OrderStatus, OrderStatusChangeEvent> {

    /**
     * 初始化配置
     */
    @Override
    public void configure(StateMachineStateConfigurer<OrderStatus, OrderStatusChangeEvent> configurer) throws Exception {
        configurer.withStates()
                .initial(OrderStatus.WAIT_PAYMENT)
                .states(EnumSet.allOf(OrderStatus.class));
    }

    /**
     * 状态流转及相应时间配置
     */
    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatus, OrderStatusChangeEvent> transitions) throws Exception {
        transitions
                .withExternal().source(OrderStatus.WAIT_PAYMENT).target(OrderStatus.WAIT_DELIVER).event(OrderStatusChangeEvent.PAY)
                .and()
                .withExternal().source(OrderStatus.WAIT_DELIVER).target(OrderStatus.WAIT_RECEIVE).event(OrderStatusChangeEvent.DELIVERY)
                .and()
                .withExternal().source(OrderStatus.WAIT_RECEIVE).target(OrderStatus.FINISH).event(OrderStatusChangeEvent.RECEIVE);
    }


    /**
     * 持久化配置
     * 实际使用中，可以配合redis等，进行持久化操作
     */
    @Bean
    public StateMachinePersister<OrderStatus, OrderStatusChangeEvent, Order> persister(CacheTemplate cacheTemplate) {
        return new DefaultStateMachinePersister<>(new StateMachinePersist<>() {
            @Override
            public void write(StateMachineContext<OrderStatus, OrderStatusChangeEvent> context, Order order) throws Exception {
                cacheTemplate.put(String.valueOf(order.getId()), order);
            }

            @Override
            public StateMachineContext<OrderStatus, OrderStatusChangeEvent> read(Order order) throws Exception {
                Object o = cacheTemplate.get(String.valueOf(order.getId()));
                if (o == null) {
                    return null;
                }
                //此处直接获取order中的状态，其实并没有进行持久化读取操作
                Order value = (Order) o;
                return new DefaultStateMachineContext<>(value.getStatus(), null, null, null);
            }
        });
    }


}
