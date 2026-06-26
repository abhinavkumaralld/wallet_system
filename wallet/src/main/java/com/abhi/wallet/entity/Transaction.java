package com.abhi.wallet.entity;

import com.abhi.wallet.enums.TransactionStatus;
import com.abhi.wallet.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction", uniqueConstraints = {
        @UniqueConstraint(name = "ref_id",columnNames = {"type","reference_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long walletId;

    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private Long senderUserId;
    private  Long receiverUserId;

    @NonNull
//    @Column(unique = true)
    private String referenceId;
    private String description;
    private LocalDateTime createdAt;

    @PrePersist
    public void createdAt(){
        this.createdAt=LocalDateTime.now();
    }
}

