package com.wsb.sellinone.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsb.sellinone.common.ApiResponse;
import com.wsb.sellinone.common.ErrorResponse;
import com.wsb.sellinone.dto.user.LoginResponseDto;
import com.wsb.sellinone.entity.user.UserEntity;
import com.wsb.sellinone.repository.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class JwtAuthenticationFilter
        extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    private ObjectMapper mapper = new ObjectMapper();

    public JwtAuthenticationFilter(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtProvider jwtProvider
    ) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }


    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request
            , HttpServletResponse response
    ) throws AuthenticationException {

        log.info("attemptAuthentication request: {}, {}",
                request.getParameter("username"),
                request.getParameter("password")
        );

        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(
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
    ) throws IOException{

        ApiResponse apiResponse;
        String resString;

        JwtUserDetails jwtUserDetails
                = (JwtUserDetails) authResult.getPrincipal();

        String token = jwtProvider.createToken(
                jwtUserDetails.getUsername(),
                jwtUserDetails.getUserEntity().getRoles()
        );

        /* 헤더에 토큰 추가 (헤더랑 바디 모두 토큰 넣어줌) */
        response.addHeader(
                JwtProperties.HEADER_STRING,
                JwtProperties.TOKEN_PREFIX + token
        );

        /* 유저 정보 추가 (간단한 내용만 추가) */
        UserEntity userEntity = jwtUserDetails.getUserEntity();
        LoginResponseDto responseUserDto = LoginResponseDto.builder()
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .name(userEntity.getName())
                .token(JwtProperties.TOKEN_PREFIX + token)
                .build()
                ;

        /* 바디설정 추가 */
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        /* apiResponse 내용 추가 */
        apiResponse = new ApiResponse(
                HttpStatus.OK.value(),
                "로그인 성공"
        );

        apiResponse.putData("data", responseUserDto);
        resString = mapper.writeValueAsString(apiResponse);

        response.getWriter().write(resString);
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed
    ) throws IOException, ServletException {

        /* 바디설정 추가 */
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        /* apiResponse 내용 추가 */
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase()
        );

        String resString = mapper.writeValueAsString(errorResponse);
        response.getWriter().write(resString);
//        super.unsuccessfulAuthentication(request, response, failed);
    }
}
