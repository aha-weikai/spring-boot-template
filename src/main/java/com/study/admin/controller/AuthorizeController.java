package com.study.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.study.admin.entity.RestBean;
import com.study.admin.service.AccountService;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthorizeController {

  @Resource
  AccountService service;

  @GetMapping("/ask-code")
  public RestBean<Void> getMethodName(
    @RequestParam String email,
    @RequestParam String type,
    HttpServletRequest request
  ) {
    String message = service.registerEmailVerifyCode(
      type,
      email,
      request.getRemoteAddr()
    );
    return message != null
      ? RestBean.success()
      : RestBean.failure(400, message);
  }
}
