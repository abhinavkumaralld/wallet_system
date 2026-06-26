package com.abhi.auth.repository;

import com.abhi.auth.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;


import java.util.Optional;


@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

    Optional<RefreshToken> findByRefreshToken(String token);



    Optional<RefreshToken>  findByUserId(Long id);

    @Modifying
    @Transactional
    void deleteAllByUserId(Long userId);
}
