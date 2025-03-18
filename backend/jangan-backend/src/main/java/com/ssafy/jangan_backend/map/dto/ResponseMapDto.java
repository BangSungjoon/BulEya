package com.ssafy.jangan_backend.map.dto;

import lombok.*;

import java.util.Map;

@Getter
public class ResponseMapDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Mobile {
        private int floor;
        private String imageUrl;
    }
}
