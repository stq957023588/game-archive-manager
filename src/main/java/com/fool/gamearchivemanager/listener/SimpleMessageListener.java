package com.fool.gamearchivemanager.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Random;

@Slf4j
public class SimpleMessageListener implements ChannelAwareMessageListener {

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String body = new String(message.getBody());
        try {
            // 获取消息内容
            log.info("Message info:{}", body);
            Thread.sleep(1000L);
            log.info("Message(deliveryTag:{}) confirmed!", deliveryTag);
            // 处理消息，假设处理成功
            // 如果处理成功，确认消息
            channel.basicAck(deliveryTag, false);  // 手动确认消息

            // 如果处理失败，使用 channel.basicNack 或 channel.basicReject 进行拒绝或重试
        } catch (Exception e) {
            // 如果处理失败，可以使用 basicNack 拒绝并重新入队
            try {
                log.info("Update tag！Message({}) rejected!", body);
                // 是否确认多条消息
                boolean multiple = false;
                // 是否重新入队
                boolean requeue = true;
                // deliveryTag: 对应消息ID，
                channel.basicNack(deliveryTag, multiple, requeue);
                // channel.basicReject(deliveryTag, requeue);
            } catch (Exception ex) {
                log.error("", ex);
            }
        }
    }
}
