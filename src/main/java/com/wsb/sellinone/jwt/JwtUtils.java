package com.wsb.sellinone.jwt;

import io.jsonwebtoken.Jwts;

public class JwtUtils {


    /**
     * 토큰에서 username 찾아서 반환
     *
     * @param token
     * @return username
     */
    public static String getUsername(String token){
        return Jwts.parserBuilder().setSigningKeyResolver(SigningKeyResolver.instance)
                .build()
                .parseClaimsJwt(token)
                .getBody()
                .getSubject();
    }

    public static String createToken(JwtUserDetails user){

        return "test";
    }

}
