package com.abhi.auth.security;

import com.abhi.auth.exception.UnauthorizedException;
import com.abhi.auth.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
@Component
public class JwtFilter extends OncePerRequestFilter{

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth"); // skip signup & login
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{

//            String header= request.getHeader("Authorization");
//            System.out.println(header+"jjjjjjjjjjj");
//            if(!header.startsWith("Bearer ")){
//                throw new UnauthorizedException("Header with Bearer token not found");
//            }
//            String token=header.split("Bearer ")[1];
//            if(token==null){
//                throw  new UnauthorizedException("Token not found");
//            }
//            Long userId= Long.valueOf(jwtUtil.getClaims(token).getSubject());
//            if(userId==null){
//                throw  new UnauthorizedException("Token not valid");
//            }
//            if(!jwtUtil.validateToken(token,userId)){
//                throw  new UnauthorizedException("Token not valid");
//            }

//            SKIPPED As WE are not calling any auth api which need jwt validation
//            UserDetails userDetails=customUserDetailsService.loadUserByUsername(userId);
//            UsernamePasswordAuthenticationToken token1=new UsernamePasswordAuthenticationToken(
//                    userDetails,null,userDetails.getAuthorities()
//            );
//            token1.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//            SecurityContextHolder.getContext().setAuthentication(token1);

            // token will be validated at api gateway
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=
                    new UsernamePasswordAuthenticationToken(
                            request.getHeader("X-User-Id"),null,null);
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            filterChain.doFilter(request,response);

        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request,response,null,e);
        }
    }
}
