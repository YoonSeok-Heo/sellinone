package com.wsb.sellinone.jwt;

import com.wsb.sellinone.entity.user.Authority;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(this.getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
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


    /**
     * HttpServletRequest Header에서 토큰을 가져온다.
     * @param request
     * @return JWT값 리턴
     */
    public String resolveToken(HttpServletRequest request){
        return request.getHeader(JwtProperties.HEADER_STRING);
    }

    /**
     * 토큰 검증
     * @param token
     * @return boolean
     */
    public boolean vatlidateToken(String token){
        try{
            if(!token.substring(0, JwtProperties.TOKEN_PREFIX.length())
                    .equalsIgnoreCase(JwtProperties.TOKEN_PREFIX)) {
                return false;
            } else {
                token = token.split(" ")[1].trim();
            }
            Jws<Claims> claims = Jwts
                    .parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        }catch (Exception e) {
            return false;
        }
    }
}
