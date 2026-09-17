package com.nexus.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

public class SubscriptionDTOs {

    @Data
    public static class SubscriptionRequest {
        private String serviceName;
        private BigDecimal price;
        private LocalDate dueDate;
        private Long categoryId;
        private String imageUrl;
    }

    @Data
    @Builder
    public static class SubscriptionResponse {
        private Long id;
        private String serviceName;
        private BigDecimal price;
        private LocalDate dueDate;
        private String status;
        private String categoryName;
        private String imageUrl;
    }
}
