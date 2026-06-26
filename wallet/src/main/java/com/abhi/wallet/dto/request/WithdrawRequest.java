package com.abhi.wallet.dto.request;

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
public class WithdrawRequest {
//    private Long userId;
    private BigDecimal amount;
    private String referenceId;
    private String description;

}
