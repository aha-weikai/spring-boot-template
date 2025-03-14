package com.study.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.admin.entity.dto.Account;
import com.study.admin.mapper.AccountMapper;
import com.study.admin.service.AccountService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl
  extends ServiceImpl<AccountMapper, Account>
  implements AccountService {

  @Override
  public UserDetails loadUserByUsername(String username) {
    Account account = this.findAccountByNameOrEmail(username);
    if (account == null) {
      throw new UsernameNotFoundException("用户名或密码错误");
    }
    return User.withUsername(username)
      .password(account.getPassword())
      .roles(account.getRole())
      .build();
  }

  public Account findAccountByNameOrEmail(String text) {
    return this.query().eq("username", text).or().eq("email", text).one();
  }
}
