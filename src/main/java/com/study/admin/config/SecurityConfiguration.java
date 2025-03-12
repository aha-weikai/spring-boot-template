package com.study.admin.config;

import com.study.admin.entity.RestBean;
import com.study.admin.entity.vo.AuthorizeVO;
import com.study.admin.filter.JwtAuthorizeFilter;
import com.study.admin.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  @Resource
  JwtAuthorizeFilter jwtAuthorizeFilter;

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
      .authorizeHttpRequests(requests ->
        requests
          .requestMatchers("/auth/**")
          .permitAll()
          .anyRequest()
          .authenticated()
      )
      .formLogin(login ->
        login
          .loginProcessingUrl("/auth/login")
          .successHandler(this::onAuthenticationSuccess)
          .failureHandler(this::onAuthenticationFailure)
      )
      .logout(logout ->
        logout
          .logoutUrl("/auth/logout")
          .logoutSuccessHandler(this::onLogoutSuccess)
      )
      .exceptionHandling(config ->
        config
          .authenticationEntryPoint(this::onUnauthorized)
          .accessDeniedHandler(this::onAccessDeny)
      )
      .csrf(csrf -> csrf.disable())
      .sessionManagement(config ->
        config.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )
      .addFilterBefore(
        jwtAuthorizeFilter,
        UsernamePasswordAuthenticationFilter.class
      )
      .build();
  }

  @Resource
  JwtUtils jwtUtil;

  public void onAuthenticationSuccess(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication
  ) throws IOException, ServletException {
    response.setContentType("application/json");
    User user = (User) authentication.getPrincipal();
    String token = jwtUtil.createJwt(user, 1, "小明");
    AuthorizeVO vo = new AuthorizeVO();
    vo.setToken(token);
    vo.setExpire(jwtUtil.expireTime());
    vo.setUsername(user.getUsername());

    response.getWriter().write(RestBean.success(vo).asJsonString());
  }

  public void onAuthenticationFailure(
    HttpServletRequest request,
    HttpServletResponse response,
    AuthenticationException exception
  ) throws IOException, ServletException {
    response.setContentType("application/json");
    response
      .getWriter()
      .write(RestBean.failure(401, exception.getMessage()).asJsonString());
  }

  public void onUnauthorized(
    HttpServletRequest request,
    HttpServletResponse response,
    AuthenticationException exception
  ) throws IOException, ServletException {
    response.setContentType("application/json");
    response
      .getWriter()
      .write(RestBean.failure(401, exception.getMessage()).asJsonString());
  }

  public void onAccessDeny(
    HttpServletRequest request,
    HttpServletResponse response,
    AccessDeniedException accessDeniedException
  ) throws IOException, ServletException {
    response.setContentType("application/json");
    response
      .getWriter()
      .write(
        RestBean.failure(403, accessDeniedException.getMessage()).asJsonString()
      );
  }

  public void onLogoutSuccess(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication
  ) throws IOException, ServletException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException(
      "Unimplemented method 'onLogoutSuccess'"
    );
  }
}
