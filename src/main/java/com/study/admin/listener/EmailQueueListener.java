package com.study.admin.listener;

import jakarta.annotation.Resource;
import java.util.Map;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "email")
public class EmailQueueListener {

  @Resource
  JavaMailSender emailSender;

  @Value("${spring.mail.username}")
  String username;

  @RabbitHandler
  public void sendEmailMessage(Map<String, Object> data) {
    String email = data.get("email").toString();
    Integer code = (Integer) data.get("code");
    String type = data.get("type").toString();
    SimpleMailMessage message =
      switch (type) {
        case "register" -> createMessage(
          "欢迎注册我们的网站",
          "您的邮件注册验证码为" +
          code +
          ",有效时间3分钟，为了保障您的安全，请勿向他人泄露验证信息。",
          email
        );
        case "reset" -> createMessage(
          "重置密码",
          "您的邮件重置密码验证码为" +
          code +
          ",有效时间3分钟，为了保障您的安全，请勿向他人泄露验证信息。",
          email
        );
        default -> null;
      };

    if (message != null) {
      emailSender.send(message);
    }
  }

  private SimpleMailMessage createMessage(
    String title,
    String content,
    String email
  ) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(username);
    message.setTo(email);
    message.setSubject(title);
    message.setText(content);
    return message;
  }
}
