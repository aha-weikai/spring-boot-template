package com.study.admin;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class AdminApplicationTests {

    @Test
    void contextLoads() {
      System.out.println("=============");
      System.out.println(new BCryptPasswordEncoder().encode("123456"));
      // $2a$10$coMlgXR1j6ZZAOKjV10.RucaFREakHniLruAqgt4XBYKFqqBaH1/e
    }

}
