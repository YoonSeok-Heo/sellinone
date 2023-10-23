package com.wsb.sellinone.jwt;

import com.wsb.sellinone.repository.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


import java.io.IOException;

/**
 * JWT를 이용한 인증
 */
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public JwtAuthorizationFilter(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtProvider jwtProvider) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        String token = null;
        String header;

        try {
            // header 에서 JWT token을 가져옵니다.
            header = request.getHeader(JwtProperties.HEADER_STRING);
            if (header == null
                    || !header.startsWith(JwtProperties.TOKEN_PREFIX)
            ) {
                chain.doFilter(request, response);
                return;
            }
            token = request.getHeader(JwtProperties.HEADER_STRING)
                    .replace(JwtProperties.TOKEN_PREFIX, "");
        } catch (Exception e) {
            log.error("토큰 가져오는 중 에러 : {} ", e.getMessage());
        }

        if (token != null) {
            Authentication authentication
                    = getUsernamePasswordAuthenticationToken(token);
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    /**
     * JWT 토큰으로 User를 찾아서 UsernamePasswordAuthenticationToken를 만들어서 반환한다.
     * User가 없다면 null
     */
    private Authentication getUsernamePasswordAuthenticationToken(
            String token
    ) {

        String username = jwtProvider.getUsername(token);
        if (username != null) {
            return jwtProvider.getAuthentication(token);
        }
        return null; // 유저가 없으면 NULL
    }
}