package com.wsb.sellinone.jwt;

/**
 * JWT 기본 설정값
 */
public class JwtProperties {
    public static final long EXPIRATION_TIME = 36000000; // 10시간
    public static final String HEADER_STRING = "JWT-AUTHENTICATION";
    public static final String TOKEN_PREFIX = "Bearer ";
}