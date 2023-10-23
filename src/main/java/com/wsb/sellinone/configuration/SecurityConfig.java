package com.wsb.sellinone.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsb.sellinone.common.ErrorResponse;
import com.wsb.sellinone.jwt.JwtAuthenticationFilter;
import com.wsb.sellinone.jwt.JwtAuthorizationFilter;
import com.wsb.sellinone.jwt.JwtProvider;
import com.wsb.sellinone.repository.user.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    private ObjectMapper mapper = new ObjectMapper();

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {
        http.httpBasic(HttpBasicConfigurer::disable)
                .csrf(CsrfConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(configurer ->
                        configurer.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))
                .apply(new MyCustomDsl())
                .and()
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers(HttpMethod.POST, "/user/login")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/join")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/role")
                        .permitAll()
                        .requestMatchers("/accessDeniedPage")
                        .permitAll()
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .exceptionHandling((exception) -> exception
                        .accessDeniedHandler(new AccessDeniedHandler() {
                            @Override
                            public void handle(
                                    HttpServletRequest request,
                                    HttpServletResponse response,
                                    AccessDeniedException accessDeniedException
                            ) throws IOException {

                                String resString;
                                ErrorResponse errorResponse = new ErrorResponse(
                                        HttpStatus.FORBIDDEN.value(),
                                        accessDeniedException.getMessage()
                                );
                                resString = mapper.writeValueAsString(
                                        errorResponse
                                );

                                log.info("accessDeniedException: {}",
                                        accessDeniedException.getMessage());

                                // 권한 문제가 발생했을 때 이 부분을 호출한다.
                                response.setStatus(HttpStatus.FORBIDDEN.value());
                                response.setCharacterEncoding("utf-8");
                                response.setContentType("application/json");
                                response.getWriter().write(resString);
                            }
                        })
                        .authenticationEntryPoint(new AuthenticationEntryPoint() {
                            @Override
                            public void commence(
                                    HttpServletRequest request,
                                    HttpServletResponse response,
                                    AuthenticationException authException
                            ) throws IOException {

                                String resString;
                                ErrorResponse errorResponse = new ErrorResponse(
                                        HttpStatus.UNAUTHORIZED.value(),
                                        authException.getMessage()
                                );
                                resString = mapper.writeValueAsString(
                                        errorResponse
                                );

                                log.info("authException: {}",
                                        authException.getMessage());

                                // 인증문제가 발생했을 때 이 부분을 호출한다.
                                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                                response.setCharacterEncoding("utf-8");
                                response.setContentType("application/json");
                                response.getWriter().write(resString);
                            }
                        })
                )
//                .addFilter((Filter) loginFailHandler())
        ;
        return http.build();
    }

    public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            http
                    .addFilter(new JwtAuthenticationFilter(authenticationManager, userRepository, jwtProvider))
                    .addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository, jwtProvider))
            ;
        }

        public MyCustomDsl customDsl() {
            return new MyCustomDsl();
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
