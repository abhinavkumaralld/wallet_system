package com.abhi.notification.dto;

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
public class TransferEvent {
    private Long senderUserId;
    private Long receiverUserId;
    private BigDecimal amount;
    private String referenceId;
    private LocalDateTime transferredAt;
}
