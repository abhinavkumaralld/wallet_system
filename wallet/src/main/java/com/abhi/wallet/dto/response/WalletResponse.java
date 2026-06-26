package com.abhi.wallet.dto.response;

import com.abhi.wallet.enums.TransactionStatus;
import com.abhi.wallet.enums.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletResponse  implements Serializable {
    private Long id;

    private Long userId;

    private BigDecimal balance;

    private Long version;

    private LocalDateTime createdAt;
}
