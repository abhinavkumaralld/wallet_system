package com.abhi.wallet.dto.response;

import com.abhi.wallet.enums.TransactionStatus;
import com.abhi.wallet.enums.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse {

    private Long id;

    private Long walletId;
    private Long senderUserId;
    private  Long receiverUserId;

    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private String referenceId;
    private String description;
    private LocalDateTime createdAt;
}
