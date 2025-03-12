package com.study.admin.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

  @Value("${spring.security.jwt.key}")
  String key;

  public String createJwt(UserDetails details, int id, String username) {
    Date expire = expireTime();
    Algorithm algorithm = Algorithm.HMAC256(key);
    return JWT.create()
      .withClaim("id", id)
      .withClaim("name", username)
      .withClaim(
        "authorities",
        details
          .getAuthorities()
          .stream()
          .map(GrantedAuthority::getAuthority)
          .toList()
      )
      .withExpiresAt(expire)
      .withIssuedAt(new Date())
      .sign(algorithm);
  }

  @Value("${spring.security.jwt.expire}")
  int expire;

  public Date expireTime() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.HOUR, expire * 24);
    return calendar.getTime();
  }

  public DecodedJWT resolveJwt(String headerToken) {
    String token = convertToken(headerToken);
    if (token == null) return null;
    Algorithm algorithm = Algorithm.HMAC256(key);
    JWTVerifier jwtVerifier = JWT.require(algorithm).build();
    DecodedJWT verify = jwtVerifier.verify(token);
    Date expiresAt = verify.getExpiresAt();
    return expiresAt.before(new Date()) ? null : verify;
  }

  private String convertToken(String headerToken) {
    if (headerToken == null || !headerToken.startsWith("Bearer ")) {
      return null;
    }
    return headerToken.substring(7);
  }

  public UserDetails toUser(DecodedJWT jwt) {
    Map<String, Claim> claims = jwt.getClaims();
    return User.withUsername(claims.get("name").asString())
      .password("******")
      .authorities(claims.get("authorities").asArray(String.class))
      .build();
  }

  public Integer toId(DecodedJWT jwt) {
    return jwt.getClaims().get("id").asInt();
  }
}
