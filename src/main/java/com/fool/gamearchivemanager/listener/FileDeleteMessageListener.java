package com.fool.gamearchivemanager.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

import java.io.File;
import java.util.Random;

@Slf4j
public class FileDeleteMessageListener implements ChannelAwareMessageListener {

    @Override
    public void onMessage(Message message) {
        // 获取消息内容
        String body = new String(message.getBody());
        log.info("delete file {}", body);
        File file = new File(body);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                log.warn("file delete failed！filepath：{}", body);
            }
        }
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            onMessage(message);
            // 处理消息，假设处理成功
            // 如果处理成功，确认消息
            long deliveryTag = message.getMessageProperties().getDeliveryTag();
            channel.basicAck(deliveryTag, false);  // 手动确认消息

            // 如果处理失败，使用 channel.basicNack 或 channel.basicReject 进行拒绝或重试
        } catch (Exception e) {
            // 如果处理失败，可以使用 basicNack 拒绝并重新入队
            try {
                long deliveryTag = message.getMessageProperties().getDeliveryTag();
                channel.basicNack(deliveryTag, false, true);  // 将消息重新入队
            } catch (Exception ex) {
                log.error("", ex);
            }
        }
    }

}
