package com.wsb.sellinone.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsb.sellinone.common.ApiResponse;
import com.wsb.sellinone.dto.user.LoginResponseDto;
import com.wsb.sellinone.entity.user.UserEntity;
import com.wsb.sellinone.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    private ObjectMapper mapper = new ObjectMapper();

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, JwtProvider jwtProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }


    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request
            , HttpServletResponse response
    ) throws AuthenticationException {
        log.info("attemptAuthentication request: {}, {}", request.getParameter("username"), request.getParameter("password"));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getParameter("username"),
                request.getParameter("password"),
                new ArrayList<>()
        );
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request
            , HttpServletResponse response
            , FilterChain chain
            , Authentication authResult
    ) throws IOException, ServletException {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authResult.getPrincipal();
        log.info("successfulAuthentication : {}", jwtUserDetails.getUserEntity().toString());
        String token = jwtProvider.createToken(jwtUserDetails.getUsername(), jwtUserDetails.getUserEntity().getRoles());
        log.info("successfulAuthentication token : {}", token);

        // 헤더에 토큰 추가
        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + token);

        // 바디에 추가 리스폰스 내용 추가
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        ApiResponse apiResponse = new ApiResponse(200, "로그인 성공");

        // 유저 정보 추가
        UserEntity userEntity = jwtUserDetails.getUserEntity();
        LoginResponseDto responseUserDto = LoginResponseDto.builder()
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .name(userEntity.getName())
                .token(JwtProperties.TOKEN_PREFIX + token)
                .build()
                ;

        // 데이터 넣기
        apiResponse.putData("data", responseUserDto);

        String resString = mapper.writeValueAsString(apiResponse);
        response.getWriter().write(resString);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
