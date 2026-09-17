package com.nexus.backend.dto;

import lombok.Builder;
import lombok.Data;

public class UserDTOs {
    @Data
    @Builder
    public static class UserProfileResponse {
        private String name;
        private String email;
        private String profileImageUrl;
    }
}
