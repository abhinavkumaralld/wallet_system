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
public class TransferEvent {
    private Long senderUserId;
    private Long receiverUserId;
    // Adding new below
    private String senderName;
    private String senderEmail;
    private String receiverName;
    private String receiverEmail;

    private BigDecimal amount;
    private String referenceId;
    private LocalDateTime transferredAt;
}
