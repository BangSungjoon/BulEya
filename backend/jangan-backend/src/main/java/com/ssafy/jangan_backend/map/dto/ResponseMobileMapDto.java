package com.ssafy.jangan_backend.map.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMobileMapDto {
    private int floor;
    private String imageUrl;
}
