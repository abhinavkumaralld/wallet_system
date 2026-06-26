package com.abhi.wallet.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private String accToken="hgfghjklwrjhshshjjrjwejrewr we r rweruweruwerurureu e";


    public SecretKey getAccKey(){
        return Keys.hmacShaKeyFor(accToken.getBytes(StandardCharsets.UTF_8));
    }

    public Claims getClaims(String accToken){
        try {
           return  Jwts.parserBuilder()
                    .setSigningKey(getAccKey())
                    .build()
                    .parseClaimsJws(accToken)
                    .getBody();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validateToken(String accToken){
        Claims claims=getClaims(accToken);
        return !claims.getExpiration().before(new Date());
    }
}
