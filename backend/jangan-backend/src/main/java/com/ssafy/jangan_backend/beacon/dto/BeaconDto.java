package com.ssafy.jangan_backend.beacon.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ssafy.jangan_backend.beacon.entity.Beacon;
import com.ssafy.jangan_backend.map.entity.Map;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // null값 빼고 전송
public class BeaconDto {
    private Integer beaconId;
    private Integer stationId;
    private Integer floor;
    private Integer beaconCode;
    private String name;
    private Integer coordX;
    private Integer coordY;
    private boolean isExit;
    private boolean isCctv;
    private String cctvIp;

    public Beacon toEntity(Map map) {
        return Beacon.builder()
                .map(map)
                .beaconCode(this.beaconCode)
                .name(this.name)
                .coordX(this.coordX)
                .coordY(this.coordY)
                .isExit(this.isExit)
                .isCctv(this.isCctv)
                .cctvIp(this.cctvIp)
                .build();
    }

    public static BeaconDto toDto(Beacon beacon) {
        return BeaconDto.builder()
                .beaconId(beacon.getId())
                .build();
    }
}


