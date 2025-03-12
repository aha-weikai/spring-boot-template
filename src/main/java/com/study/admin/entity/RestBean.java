package com.study.admin.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

public record RestBean<T>(int code, T data, String message) {
  public static <T> RestBean<T> success(T data) {
    return new RestBean<>(200, data, "请求成功");
  }

  public static <T> RestBean<T> success() {
    return success(null);
  }

  public static <T> RestBean<T> failure(int code, String message) {
    return new RestBean<>(code, null, message);
  }

  

  /**
   * 封装一个 Json 字符串的实现
   * 将 RestBean 对象转换为 JSON 字符串
   * @return JSON 字符串
   */

  public String asJsonString() {
    return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
  }
}
