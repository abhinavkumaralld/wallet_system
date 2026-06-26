package com.abhi.wallet.security;

import com.abhi.wallet.exception.BadRequestException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.kafka.common.security.auth.AuthenticationContext;
import org.apache.kafka.common.security.auth.PlaintextAuthenticationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.ContentHandlerFactory;
@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

//        String header=request.getHeader("Authorization");
//        if(header==null){
//            throw new BadRequestException("No Authorization header");
//        }
//        String token=header.split("Bearer ")[1];
//        if(token==null){
//            throw new BadRequestException("No token sent");
//        }
//        if(!jwtUtil.validateToken(token)){
//            throw new BadRequestException("No token sent");
//        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=
                new UsernamePasswordAuthenticationToken(
                        request.getHeader("X-User-Id"),null,null);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        filterChain.doFilter(request,response);

    }
}
