package com.fool.gamearchivemanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fool.gamearchivemanager.entity.po.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {
}
