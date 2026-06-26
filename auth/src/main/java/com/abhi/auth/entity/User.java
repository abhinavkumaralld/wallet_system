package com.abhi.auth.entity;

import com.abhi.auth.common.ApiResponse;
import com.abhi.auth.common.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "abhi_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = false)
    private  String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role= Role.USER;

    private LocalDateTime createdAt;

    @PrePersist
    private  void prePersist(){
        this.createdAt=LocalDateTime.now();
    }

}