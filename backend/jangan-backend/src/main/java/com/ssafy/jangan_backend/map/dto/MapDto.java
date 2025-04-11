package com.ssafy.jangan_backend.map.dto;

import com.ssafy.jangan_backend.map.entity.Map;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class MapDto {
    private Integer id;
    private Integer floor;
    private Integer stationId;
    private String imageUrl;

    public static MapDto fromEntity(Map map, String imageUrl) {
        return MapDto.builder()
                .id(map.getId())
                .floor(map.getFloor())
                .stationId(map.getStation().getId())
                .imageUrl(imageUrl)
                .build();
    }
    public static MapDto fromEntity(Map map) {
        return MapDto.builder()
                .id(map.getId())
                .floor(map.getFloor())
                .stationId(map.getStation().getId())
                .build();
    }
}
