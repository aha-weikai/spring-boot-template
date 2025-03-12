package com.study.admin.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.study.admin.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthorizeFilter extends OncePerRequestFilter {

  @Resource
  private JwtUtils jwtUtils;

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {
    String authorization = request.getHeader("Authorization");
    DecodedJWT jwt = jwtUtils.resolveJwt(authorization);
    System.out.println(authorization);
    System.out.println(jwt);
    if (jwt != null) {
      UserDetails user = jwtUtils.toUser(jwt);
      UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(
          user,
          null,
          user.getAuthorities()
        );
      authenticationToken.setDetails(
        new WebAuthenticationDetailsSource().buildDetails(request)
      );
      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      System.out.println(jwtUtils.toId(jwt)+"----------");
      request.setAttribute("id", jwtUtils.toId(jwt));
    }
    filterChain.doFilter(request, response);
  }
}
