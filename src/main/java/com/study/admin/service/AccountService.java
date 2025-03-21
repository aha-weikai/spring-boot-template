package com.study.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.admin.entity.dto.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<Account>, UserDetailsService {
  Account findAccountByNameOrEmail(String text);

  String registerEmailVerifyCode(String type, String email, String ip);
}
