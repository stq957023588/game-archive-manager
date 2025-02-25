package com.fool.gamearchivemanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fool.gamearchivemanager.entity.po.GameArchive;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GameMapper extends BaseMapper<GameArchive> {


    int save(GameArchive entity);

}
