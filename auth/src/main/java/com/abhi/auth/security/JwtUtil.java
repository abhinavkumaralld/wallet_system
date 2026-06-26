package com.abhi.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
@Component
public class JwtUtil {

    public String SECRET_KEY="abhinavKumarIsAVeryGoodGuy.HeLovesHerButSheMightNotKnowThatAbhinavLovesHer";
    public Integer EXPIRATION_TIME=1000*60*5;
    public Integer REFRESH_EXPIRATION_TIME=1000*60*10;

    public SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long id){
        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getClaims(String token){
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (ExpiredJwtException exception){
            return exception.getClaims();
        }
    }

    public boolean validateToken(String token,Long id){
        if(!id.toString().equals(getClaims(token).getSubject())){
            return false ; // username not matched
        }
        if(getClaims(token).getExpiration().before(new Date())){
            return false; // token expired
        }
        return true;
    }
}
