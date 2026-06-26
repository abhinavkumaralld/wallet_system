package com.abhi.wallet.dto.request;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositRequest {
//    private Long userId;
    private BigDecimal amount;
    private String referenceId;
    private String description;
}

