package com.fool.gamearchivemanager.config.message.queue;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class RabbitMessageQueueTemplate implements MessageQueueTemplate {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMessageQueueTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(String key, Object o) {
        rabbitTemplate.convertAndSend(key, "", o);
    }

}
