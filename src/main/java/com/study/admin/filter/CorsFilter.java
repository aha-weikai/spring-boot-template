package com.study.admin.filter;

import com.study.admin.utils.Const;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 跨域过滤器配置
 * 因为之后需要做限流，所以跨域过滤器手写，而不是用spring security的跨域过滤器
 */
@Component
@Order(Const.ORDER_CORS)
public class CorsFilter extends HttpFilter {

  @Override
  protected void doFilter(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain chain
  ) throws IOException, ServletException {
    addCorsHeader(request, response);
    chain.doFilter(request, response);
  }

  private void addCorsHeader(
    HttpServletRequest request,
    HttpServletResponse response
  ) {
    response.setHeader(
      "Access-Control-Allow-Origin",
      request.getHeader("Origin")
    );
    response.setHeader(
      "Access-Control-Allow-Methods",
      "POST, GET, OPTIONS, DELETE, PUT"
    );
    response.setHeader(
      "Access-Control-Allow-Headers",
      "Authorization,Content-Type"
    );
  }
}
