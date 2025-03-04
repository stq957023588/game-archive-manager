package com.fool.gamearchivemanager.module.archive.controller;

import com.fool.gamearchivemanager.entity.dto.AccountDTO;
import com.fool.gamearchivemanager.entity.po.GameArchive;
import com.fool.gamearchivemanager.module.archive.service.GameArchiveService;
import com.fool.gamearchivemanager.module.file.FileService;
import com.fool.gamearchivemanager.config.message.queue.MessageQueueTemplate;
import com.fool.gamearchivemanager.module.account.service.AccountService;
import com.fool.gamearchivemanager.util.SecurityContextUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class GameArchiveController {

    private final AccountService accountService;
    private final GameArchiveService gameArchiveService;
    private final FileService fileService;
    private final MessageQueueTemplate messageQueueTemplate;

    public GameArchiveController(AccountService accountService, GameArchiveService gameArchiveService, FileService fileService, MessageQueueTemplate messageQueueTemplate) {
        this.accountService = accountService;
        this.gameArchiveService = gameArchiveService;
        this.fileService = fileService;
        this.messageQueueTemplate = messageQueueTemplate;
    }

    /**
     * 查询最新存档
     *
     * @param gameName 游戏名
     * @return 最新存档信息
     */
    @GetMapping("/game/archive/latest")
    public GameArchive latest(String gameName) {
        String username = SecurityContextUtils.getUsername();
        AccountDTO user = accountService.getUser(username);
        return gameArchiveService.latestArchiveDetail(user.getUid(), gameName);
    }

    @GetMapping("/game/archive/list")
    public List<GameArchive> list(String gameName) {
        String username = SecurityContextUtils.getUsername();
        AccountDTO account = accountService.getUser(username);
        return gameArchiveService.list(account.getUid(), gameName);
    }


    @PostMapping("/game/archive")
    public GameArchive saveArchive(@RequestBody GameArchive gameArchive) {
        String username = SecurityContextUtils.getUsername();
        AccountDTO user = accountService.getUser(username);

        String gameArchiveFilePrefix = String.format("archive/%08d/%s", user.getUid(), gameArchive.getGameName());

        String uuid = UUID.randomUUID().toString().replace("-", "");
        String filePath = String.format("%s/%s.zip", gameArchiveFilePrefix, uuid);


        gameArchive.setArchivePath(filePath);
        gameArchive.setUid(user.getUid());

        gameArchiveService.save(gameArchive);
        return gameArchive;
    }

}
