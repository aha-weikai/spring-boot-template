package com.study.admin.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

  @Bean("emailQueue")
  Queue queue() {
    return QueueBuilder.durable("email").build();
  }
}
