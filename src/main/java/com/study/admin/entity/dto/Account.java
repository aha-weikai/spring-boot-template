package com.study.admin.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.study.admin.entity.BaseData;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@TableName("db_account")
@AllArgsConstructor
public class Account {

  // public class Account implements BaseData {

  @TableId(type = IdType.AUTO)
  Integer id;

  String username;
  String password;
  String email;
  String role;
  Date registerTime;
}
