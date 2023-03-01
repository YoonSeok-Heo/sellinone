package com.wsb.sellinone.jwt;

import io.jsonwebtoken.security.Keys;
import org.springframework.data.util.Pair;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Map;
import java.util.Random;

public class JwtKey {

    /**
     * KID KEY LIST 외부로 유출 금지.
     */
    private static final Map<String, String> SECRET_KEY_SET = Map.of(
            "key1", "sellinoneJWTdemosellinoneJWTdemosellinoneJWTdemo",
            "key2", "RandomsellinoneJWTRandomsellinoneJWTRandomsellinoneJWT",
            "key3", "SECURITYJwtsellinoneSECURITYJwtsellinoneSECURITYJwtsellinone"
    );

    private static final String[] KID_SET = SECRET_KEY_SET.keySet().toArray(new String[0]);

    private static Random randomIndex = new Random();

    /**
     * SECRET_KEY_SET 에서 랜덤한 KEY가져오기
     * @return kid와 key pair
     */
    public static Pair<String, Key> getRandomKey(){
        String kid = KID_SET[randomIndex.nextInt(KID_SET.length)];
        String secretKey = SECRET_KEY_SET.get(kid);
        return Pair.of(kid, Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)));
    }

    public static Key getKey(String kid){
        String key = SECRET_KEY_SET.getOrDefault(kid, null);
        if(key == null){
            return null;
        }
        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }
}
