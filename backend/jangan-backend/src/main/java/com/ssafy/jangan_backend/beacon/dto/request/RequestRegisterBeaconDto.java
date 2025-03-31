package com.ssafy.jangan_backend.beacon.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("coord_x")
    private Integer coordX;

    @JsonProperty("coord_y")
    private Integer coordY;

    private Boolean isExit;
    private Boolean isCctv;
    private String cctvIp;

    public Beacon toEntity(Map map) {
        return Beacon.builder()
                .map(map)
                .mapId(map.getId())
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
