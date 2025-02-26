package com.fool.gamearchivemanager.module.management.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fool.gamearchivemanager.entity.po.SystemSetting;
import com.fool.gamearchivemanager.mapper.SystemSettingMapper;
import org.springframework.stereotype.Service;

@Service
public class SystemSettingService {

    private final SystemSettingMapper systemSettingMapper;

    public SystemSettingService(SystemSettingMapper systemSettingMapper) {
        this.systemSettingMapper = systemSettingMapper;
    }


    public String settingValue(int code) {
        LambdaQueryWrapper<SystemSetting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemSetting::getCode, code);

        SystemSetting systemSetting = systemSettingMapper.selectOne(wrapper);
        if (systemSetting == null) {
            return null;
        }
        return systemSetting.getValue();
    }


}
