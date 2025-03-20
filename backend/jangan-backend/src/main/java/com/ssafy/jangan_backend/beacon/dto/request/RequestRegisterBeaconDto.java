package com.ssafy.jangan_backend.beacon.dto.request;

import com.ssafy.jangan_backend.beacon.entity.Beacon;
import com.ssafy.jangan_backend.map.entity.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestRegisterBeaconDto {
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
}
