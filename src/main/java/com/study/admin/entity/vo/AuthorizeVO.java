package com.study.admin.entity.vo;

import java.util.Date;
import lombok.Data;

@Data
public class AuthorizeVO {

  String username;
  String role;
  String token;
  Date expire;
}
