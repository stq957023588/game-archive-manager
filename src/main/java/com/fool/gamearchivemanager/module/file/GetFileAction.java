package com.fool.gamearchivemanager.module.file;

import com.fool.gamearchivemanager.entity.dto.AccountDTO;
import com.fool.gamearchivemanager.entity.po.GameArchive;
import com.fool.gamearchivemanager.module.archive.service.GameArchiveService;
import com.fool.gamearchivemanager.util.SpringContextUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
public class GetFileAction implements FileTransferProtocolAction {

    @Override
    public void doAction(ChannelHandlerContext ctx, Object msg, Map<String, String> header, AccountDTO user) {
        String archiveId = header.get(FileTransferProtocolHeader.ARCHIVE_ID);
        log.info("Get file action.Archive id :{}", archiveId);

        GameArchiveService gameArchiveService = SpringContextUtils.getBean(GameArchiveService.class);
        GameArchive detail = gameArchiveService.detail(archiveId);
        if (detail == null) {
            throw new RuntimeException("游戏存档信息不存在！");
        }

        File file = new File(detail.getArchivePath());
        if (!file.exists()) {
            throw new RuntimeException("游戏存档文件不存在！");
        }

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            ctx.write(Unpooled.copiedBuffer(FileTransferProtocolState.OK, CharsetUtil.UTF_8));
            ctx.write(Unpooled.copiedBuffer("\n", CharsetUtil.UTF_8));
            DefaultFileRegion defaultFileRegion = new DefaultFileRegion(raf.getChannel(), 0, raf.length());
            ctx.write(defaultFileRegion);
            ctx.flush();
        } catch (IOException e) {
            throw new RuntimeException("游戏存档文件不存在", e);
        }
    }
}
