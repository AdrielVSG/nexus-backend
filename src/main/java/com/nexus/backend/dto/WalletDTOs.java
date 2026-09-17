package com.nexus.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class WalletDTOs {

    @Data
    @Builder
    public static class WalletResponse {
        private BigDecimal balance;
        private List<TransactionResponse> recentTransactions;
    }

    @Data
    @Builder
    public static class TransactionResponse {
        private Long id;
        private String description;
        private BigDecimal amount;
        private String type;
        private LocalDateTime timestamp;
    }

    @Data
    public static class DepositRequest {
        private BigDecimal amount;
    }
}
