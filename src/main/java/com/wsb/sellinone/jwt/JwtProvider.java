package com.wsb.sellinone.jwt;

import com.wsb.sellinone.entity.user.Authority;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret.key}")
    private String key;

    private Key secretKey;

    private final JwtUserDetailsService jwtUserDetailsService;

    @PostConstruct
    protected void init(){
        secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * JWT 토큰 생성하는 부분
     * @param username
     * @param roles
     * @return
     */
    public String createToken(String username, List<Authority> roles){
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + JwtProperties.EXPIRATION_TIME))
                .signWith(secretKey, SignatureAlgorithm.ES256)
                .compact();
    }

    /**
     * 권한정보 획득
     * Spring Security 인증과정에서 권한확인을 위한 기능
     * @param token
     * @return
     */
    public Authentication getAuthentication(String token){
        JwtUserDetails jwtUserDetails = jwtUserDetailsService.loadUserByUsername(this.getUsername());
        return new UsernamePasswordAuthenticationToken(jwtUserDetails, "", jwtUserDetails.getAuthorities());
    }

    /**
     * 토큰에 담겨져 있는 username 획득
     * @param token
     * @return
     */
    public String getUsername(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
