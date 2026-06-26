package com.abhi.auth.service;

import com.abhi.auth.entity.User;
import com.abhi.auth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=userRepository.findByEmail(username).orElseThrow();
//        System.out.println("user"+user.toString());
        log.info("refresh token {}",user.toString());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())            // username = email
                .password(user.getPassword())        // encoded password from DB
                .roles(user.getRole().name())        // USER / ADMIN
                .build();
    }
}
