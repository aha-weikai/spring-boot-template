package com.study.admin.utils;

import jakarta.annotation.Resource;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class FlowUtils {

  @Resource
  StringRedisTemplate stringRedis;

  public boolean limitOnceCheck(String key, int blockTime) {
    if (Boolean.TRUE.equals(stringRedis.hasKey(key))) {
      return false;
    } else {
      stringRedis.opsForValue().set(key, "", blockTime, TimeUnit.SECONDS);
      return true;
    }
  }
}
