package com.fool.gamearchivemanager.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fool.gamearchivemanager.entity.po.GameArchive;
import com.fool.gamearchivemanager.module.archive.service.GameArchiveService;
import com.fool.gamearchivemanager.util.FileUtils;
import com.fool.gamearchivemanager.util.SpringContextUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 监听存档文件被保存到本地的事件
 */
@Slf4j
public class ArchiveFileSavedMessageListener implements ChannelAwareMessageListener {

    private final ObjectMapper objectMapper;

    private GameArchiveService gameArchiveService;

    public ArchiveFileSavedMessageListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ArchiveFileSavedMessageListener() {
        this.objectMapper = new ObjectMapper();
        // 设置日期格式
        this.objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 关闭日期序列化为时间戳的功能
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void onMessage(Message message) {
        // 获取消息内容
        String body = new String(message.getBody());
        log.info("Archive file saved message listener listened message:{}", message);
        try {
            GameArchive archive = objectMapper.readValue(body, GameArchive.class);
            if (gameArchiveService == null) {
                gameArchiveService = SpringContextUtils.getBean(GameArchiveService.class);
            }
            List<GameArchive> gameArchives = gameArchiveService.outOfRangeData(archive.getUid(), archive.getGameName());
            if (gameArchives.isEmpty()) {
                return;
            }
            for (GameArchive gameArchive : gameArchives) {
                FileUtils.deleteFile(gameArchive.getArchivePath());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
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
            log.error("消息处理失败！", e);
            try {
                long deliveryTag = message.getMessageProperties().getDeliveryTag();
                channel.basicNack(deliveryTag, false, true);  // 将消息重新入队
            } catch (Exception ex) {
                log.error("", ex);
            }
        }
    }
}
