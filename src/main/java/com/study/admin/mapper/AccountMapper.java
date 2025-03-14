package com.study.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.admin.entity.dto.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {}
