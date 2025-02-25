package com.fool.gamearchivemanager.module.file.controller;

import com.fool.gamearchivemanager.config.message.queue.MessageQueueTemplate;
import com.fool.gamearchivemanager.entity.constant.ExistStatus;
import com.fool.gamearchivemanager.entity.constant.MessageQueueConstant;
import com.fool.gamearchivemanager.entity.po.GameArchive;
import com.fool.gamearchivemanager.module.archive.service.GameArchiveService;
import com.fool.gamearchivemanager.util.FileUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
public class FileController {

    private final MessageQueueTemplate messageQueueTemplate;

    private final GameArchiveService gameArchiveService;

    public FileController(MessageQueueTemplate messageQueueTemplate, GameArchiveService gameArchiveService) {
        this.messageQueueTemplate = messageQueueTemplate;
        this.gameArchiveService = gameArchiveService;
    }

    @PostMapping("/game/archive/file")
    public void pushArchiveFile(@Param("archiveId") String archiveId, @Param("file") MultipartFile file) throws IOException {
        GameArchive detail = gameArchiveService.detail(archiveId);
        if (detail == null) {
            throw new RuntimeException("存档不存在");
        }
        if (detail.getArchiveFileStatus() == ExistStatus.EXIST) {
            throw new RuntimeException("存档文件已存在");
        }
        byte[] bytes = file.getBytes();
        FileUtils.write(bytes, detail.getArchivePath());
        messageQueueTemplate.send(MessageQueueConstant.EXCHANGE_FILE_SAVED, detail);
    }

    @GetMapping("/game/archive/file")
    public void getArchive(String archiveId, HttpServletResponse response) throws FileNotFoundException {
        GameArchive detail = gameArchiveService.detail(archiveId);
        if (detail == null) {
            throw new RuntimeException("存档不存在");
        }
        File file = new File(detail.getArchivePath());
        if (!file.exists()) {
            throw new FileNotFoundException("存档不存在");
        }
        try (InputStream in = new FileInputStream(file)) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8));

            ServletOutputStream outputStream = response.getOutputStream();
            FileUtils.write(in, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
