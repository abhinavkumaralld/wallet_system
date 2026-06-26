package com.abhi.gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

//  Correct class name
@Component
public class JwtAuthGatewayFilterFactory extends
        AbstractGatewayFilterFactory<Object> {

    public JwtAuthGatewayFilterFactory() {
        super(Object.class);
    }
//    @Value("${jwt.secret}")
    private String secret="abhinavKumarIsAVeryGoodGuy.HeLovesHerButSheMightNotKnowThatAbhinavLovesHer";
    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {

            ServerHttpRequest request = exchange.getRequest();
            String authHeader = request.getHeaders()
                    .getFirst("Authorization");

            // No token → reject
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse()
                        .setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);

            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(
                                secret.getBytes(StandardCharsets.UTF_8)))
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String userId = claims.getSubject();

                // Add userId as header for downstream services
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-User-Id", userId)
                        .build();

                return chain.filter(exchange.mutate()
                        .request(mutatedRequest)
                        .build());

            } catch (Exception e) {
                exchange.getResponse()
                        .setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }
}