package com.fool.gamearchivemanager.module.archive.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fool.gamearchivemanager.entity.constant.SystemSettingCode;
import com.fool.gamearchivemanager.entity.po.GameArchive;
import com.fool.gamearchivemanager.mapper.GameMapper;
import com.fool.gamearchivemanager.module.management.service.SystemSettingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameArchiveService {

    private final SystemSettingService systemSettingService;

    private final GameMapper gameMapper;

    public GameArchiveService(SystemSettingService systemSettingService, GameMapper gameMapper) {
        this.systemSettingService = systemSettingService;
        this.gameMapper = gameMapper;
    }

    public GameArchive detail(String archiveId) {
        LambdaQueryWrapper<GameArchive> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GameArchive::getId, archiveId);
        return gameMapper.selectOne(wrapper);
    }


    public GameArchive latestArchiveDetail(Integer userId, String gameName) {
        LambdaQueryWrapper<GameArchive> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GameArchive::getUid, userId)
                .eq(GameArchive::getGameName, gameName)
                .orderByDesc(GameArchive::getSaveTime)
                .last("limit 1");
        return gameMapper.selectOne(wrapper);
    }

    public List<GameArchive> outOfRangeData(Integer uid, String gameName) {
        String maxArchiveQuantity = systemSettingService.settingValue(SystemSettingCode.MAX_ARCHIVE_QUANTITY);
        LambdaQueryWrapper<GameArchive> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GameArchive::getUid, uid)
                .eq(GameArchive::getGameName, gameName)
                .orderByDesc(GameArchive::getSaveTime)
                .last(String.format("limit (%s + 1), 18446744073709551615", maxArchiveQuantity));

        return gameMapper.selectList(wrapper);
    }

    public List<GameArchive> list(Integer uid, String gameName) {
        String maxArchiveQuantity = systemSettingService.settingValue(SystemSettingCode.MAX_ARCHIVE_QUANTITY);

        LambdaQueryWrapper<GameArchive> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GameArchive::getUid, uid)
                .eq(GameArchive::getGameName, gameName)
                .orderByDesc(GameArchive::getSaveTime)
                .last("limit " + maxArchiveQuantity);

        return gameMapper.selectList(wrapper);
    }


    public void save(GameArchive gameArchive) {
        // TODO 记录存档文件上传状态
        gameMapper.save(gameArchive);
    }

}
