package com.study.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.admin.entity.dto.Account;
import com.study.admin.mapper.AccountMapper;
import com.study.admin.service.AccountService;
import com.study.admin.utils.Const;
import com.study.admin.utils.FlowUtils;
import jakarta.annotation.Resource;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl
  extends ServiceImpl<AccountMapper, Account>
  implements AccountService {

  @Resource
  AmqpTemplate amqpTemplate;

  @Resource
  StringRedisTemplate stringRedis;

  @Resource
  FlowUtils flowUtils;

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

  @Override
  public String registerEmailVerifyCode(String type, String email, String ip) {
    synchronized (ip.intern()) {
      if (!verifyLimit(ip)) {
        return "请求频繁，稍后再试";
      }
    }
    Random random = new Random();
    int code = random.nextInt(899999) + 100000;
    Map<String, Object> data = Map.of(
      "type",
      type,
      "email",
      email,
      "code",
      code
    );
    amqpTemplate.convertAndSend("email", data);
    stringRedis
      .opsForValue()
      .set(
        Const.VERIFY_EMAIL_DATA + email,
        String.valueOf(code),
        3,
        TimeUnit.MINUTES
      );
    return "1";
  }

  private boolean verifyLimit(String ip) {
    String key = Const.VERIFY_EMAIL_LIMIT + ip;
    return flowUtils.limitOnceCheck(key, 60);
  }
}
