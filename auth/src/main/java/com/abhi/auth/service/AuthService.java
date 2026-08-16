package com.abhi.auth.service;


import com.abhi.auth.common.Role;
import com.abhi.auth.dto.request.LoginRequest;
import com.abhi.auth.dto.request.RegisterRequest;
import com.abhi.auth.dto.response.UserDetailsResponse;
import com.abhi.auth.entity.RefreshToken;
import com.abhi.auth.entity.Token;
import com.abhi.auth.entity.User;
import com.abhi.auth.exception.BadRequestException;
import com.abhi.auth.repository.RefreshTokenRepository;
import com.abhi.auth.repository.UserRepository;
import com.abhi.auth.security.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public Token signup(RegisterRequest registerRequest){
        User user=userRepository.findByEmail(registerRequest.getEmail()).orElse(null);
//        System.out.println("heu reg {}"+registerRequest.toString());
        log.info("signup {}",registerRequest.toString());
        if(user!=null) throw new BadRequestException("User already exist");
        User newUser=User.builder().email(registerRequest.getEmail())
                .name(registerRequest.getName())
                .password(bCryptPasswordEncoder.encode(registerRequest.getPassword()))
                .mobile(registerRequest.getMobile())
                .role(Role.USER)
                .build();
        User user1=userRepository.save(newUser);
         return new Token(jwtUtil.generateToken(user1.getId()),null );
    }

    public Token login(LoginRequest loginRequest) {
//        System.out.println("heu reg {}"+loginRequest.toString());
        log.info("login {}",loginRequest.toString());

        User user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);

        if (user == null) throw new BadRequestException("User not exist");
        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadRequestException("Pasword not matching");
        }
        return new Token(jwtUtil.generateToken(user.getId()), generateRefreshToken(user.getId()));
    }
    public Token refrehToken(Token token){
        Long userId= Long.valueOf(jwtUtil.getClaims(token.getAccessToken()).getSubject());
        log.info("refresh token {}",userId);

        if(userId==null){
            throw new RuntimeException("invalid access token");
        }
        if(validateRefreshToken(token.getRefreshToken(), userId)){
             return new Token(jwtUtil.generateToken(userId),generateRefreshToken(userId) );

        }
        throw new RuntimeException("refresh token not not valid");

    }
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Transactional
    public String generateRefreshToken(Long userId){
        RefreshToken refreshToken1 =refreshTokenRepository.findByUserId(userId).orElse(null);
        if(refreshToken1!=null){
            refreshTokenRepository.deleteAllByUserId(userId);
        }
        log.info("generate refresh token {}",refreshToken1);

        String token= Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("tokenType","REFRESH")
                .setExpiration(new Date(System.currentTimeMillis()+jwtUtil.REFRESH_EXPIRATION_TIME))
                .signWith(jwtUtil.getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
        RefreshToken refreshToken=new RefreshToken();
        refreshToken.setRefreshToken(token);
        refreshToken.setUserId(Long.valueOf(String.valueOf(userId)));
        refreshToken.setExpiry(jwtUtil.getClaims(token).getExpiration().toInstant());
        refreshTokenRepository.save(refreshToken);
        return token;
    }
    public boolean validateRefreshToken(String token,Long userId){
        RefreshToken refreshToken =refreshTokenRepository.findByRefreshToken(token).orElse(null);
        log.info("validate refresh token {}",refreshToken);

        if(refreshToken==null){
            throw new RuntimeException("token not exist");
        } else if(!Objects.equals(refreshToken.getUserId(), userId)){
            throw new RuntimeException("token not matched");
        }else if(refreshToken.getExpiry().isBefore(new Date(System.currentTimeMillis()).toInstant())){
            System.out.println(refreshToken.getExpiry()+" _____  "+new Date(System.currentTimeMillis()).toInstant());
            throw new RuntimeException("token expired");
        }
        return true;
    }

    public UserDetailsResponse getUserDetails(Long userId) {

        System.out.println("long userid "+userId+" ");
        User user=userRepository.getById(userId);
        UserDetailsResponse userDetailsResponse= UserDetailsResponse
                .builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
        log.info(String.valueOf(userDetailsResponse));
        return userDetailsResponse;
    }
}
