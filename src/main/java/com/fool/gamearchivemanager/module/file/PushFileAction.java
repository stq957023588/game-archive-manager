package com.fool.gamearchivemanager.module.file;

import com.fool.gamearchivemanager.config.message.queue.MessageQueueTemplate;
import com.fool.gamearchivemanager.entity.constant.MessageQueueConstant;
import com.fool.gamearchivemanager.entity.dto.AccountDTO;
import com.fool.gamearchivemanager.entity.po.GameArchive;
import com.fool.gamearchivemanager.module.archive.service.GameArchiveService;
import com.fool.gamearchivemanager.util.SpringContextUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class PushFileAction implements FileTransferProtocolAction {


    @Override
    public void doAction(ChannelHandlerContext ctx, Object msg, Map<String, String> header, AccountDTO user) {
        ByteBuf byteBuf = (ByteBuf) msg;

        String gameName = header.get(FileTransferProtocolHeader.GAME_NAME);
        String md5 = header.get(FileTransferProtocolHeader.MD5);
        String saveTime = header.get(FileTransferProtocolHeader.SAVE_TIME);

        if (!StringUtils.hasText(gameName) || !StringUtils.hasText(md5) || !StringUtils.hasText(saveTime)) {
            throw new RuntimeException("缺少参数！");
        }

        String gameArchiveFilePrefix = String.format("archive/%08d/%s", user.getUid(), gameName);

        String uuid = UUID.randomUUID().toString().replace("-", "");
        String filePath = String.format("%s/%s.zip", gameArchiveFilePrefix, uuid);

        GameArchive gameArchive;
        try {
            gameArchive = new GameArchive(gameName, md5, user.getUid(), filePath, saveTime, "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        GameArchiveService gameArchiveService = SpringContextUtils.getBean(GameArchiveService.class);
        gameArchiveService.save(gameArchive);
        List<GameArchive> gameArchives = gameArchiveService.outOfRangeData(user.getUid(), gameName);

        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath);
             FileChannel fileChannel = fileOutputStream.getChannel()) {
            // 确保 ByteBuf 已经有数据
            // 将 ByteBuf 中的数据写入到文件
            ByteBuffer byteBuffer = byteBuf.nioBuffer();
            while (byteBuffer.hasRemaining()) {
                int written = fileChannel.write(byteBuffer);
                log.debug("{} bytes written", written);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // TODO 消息队列发送存档信息，而不是存档文件地址
        MessageQueueTemplate template = SpringContextUtils.getBean(MessageQueueTemplate.class);
        for (GameArchive archive : gameArchives) {
            template.send(MessageQueueConstant.EXCHANGE_TEST,archive.getArchivePath());
        }

    }
}
