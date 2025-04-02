package com.ssafy.jangan_backend.map.dto;

import com.ssafy.jangan_backend.beacon.dto.BeaconDto;
import com.ssafy.jangan_backend.map.entity.Map;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMobileMapDto {
    private int floor;
    private String imageUrl;

    public static ResponseMobileMapDto fromEntity(Map map, String imageUrl) {
        return ResponseMobileMapDto
                .builder()
                .floor(map.getFloor())
                .imageUrl(imageUrl)
                .build();
    }
}
