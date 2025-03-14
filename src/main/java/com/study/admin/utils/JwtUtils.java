package com.study.admin.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

  @Resource
  StringRedisTemplate template;

  @Value("${spring.security.jwt.key}")
  String key;

  public String createJwt(UserDetails details, int id, String username) {
    System.out.println(username);
    Date expire = expireTime();
    Algorithm algorithm = Algorithm.HMAC256(key);
    return JWT.create()
      .withJWTId(UUID.randomUUID().toString())
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

  /**
   * 解析token
   * convert : 转换
   */

  public DecodedJWT resolveJwt(String headerToken) {
    String token = convertToken(headerToken);
    if (token == null) return null;
    Algorithm algorithm = Algorithm.HMAC256(key);
    JWTVerifier jwtVerifier = JWT.require(algorithm).build();
    DecodedJWT verify = jwtVerifier.verify(token);
    if (this.isInvalidToken(verify.getId())) {
      return null;
    }
    Date expiresAt = verify.getExpiresAt();
    return expiresAt.before(new Date()) ? null : verify;
  }

  /**
   * 转换token
   * @param headerToken
   * @return
   */
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

  /**
   * 使JWT（JSON Web Token）失效
   * 该方法通过验证和解析给定的token，然后尝试删除它来使token失效
   *
   * @param headerToken 请求头中的token字符串
   * @return 如果token成功使失效，则返回true；否则返回false
   */
  public boolean invalidateJwt(String headerToken) {
    String token = this.convertToken(headerToken);
    if (token == null) {
      return false;
    }
    Algorithm algorithm = Algorithm.HMAC256(key);
    JWTVerifier jwtVerifier = JWT.require(algorithm).build();
    DecodedJWT jwt = jwtVerifier.verify(token);
    String id = jwt.getId();
    return deleteToken(id, jwt.getExpiresAt());
  }

  /**
   * 将放入黑名单内
   */
  private boolean deleteToken(String uuid, Date time) {
    if (isInvalidToken(uuid)) {
      return false;
    }
    Date now = new Date();
    long expire = Math.max(time.getTime() - now.getTime(), 0);
    template
      .opsForValue()
      .set(Const.JWT_BLACK_LIST + uuid, "", expire, TimeUnit.MILLISECONDS);
    return true;
  }

  private boolean isInvalidToken(String uuid) {
    return template.hasKey(Const.JWT_BLACK_LIST + uuid);
  }
}
