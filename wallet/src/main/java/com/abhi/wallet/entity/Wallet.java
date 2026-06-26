package com.abhi.wallet.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private Long userId;

    private BigDecimal balance;

    @Version
    private Long version;

    private LocalDateTime createdAt;

    @PrePersist
    public void createdAt(){
        this.createdAt=LocalDateTime.now();
    }

}


//id | user_id | balance | version | created_at
//
//1  |    101  |  5000   |    3    | 2024-01-01
//        2  |    102  |  2000   |    1    | 2024-01-01