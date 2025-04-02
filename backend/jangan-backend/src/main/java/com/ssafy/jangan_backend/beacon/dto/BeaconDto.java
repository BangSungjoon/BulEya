package com.ssafy.jangan_backend.beacon.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssafy.jangan_backend.beacon.entity.Beacon;
import com.ssafy.jangan_backend.map.entity.Map;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BeaconDto {
    private Integer beaconId;
    private Integer mapId;
    private Integer beaconCode;
    private String name;
    private Double coordX;
    private Double coordY;
    private String cctvIp;
    @JsonProperty("is_cctv")
    private Boolean isCctv;
    @JsonProperty("is_exit")
    private Boolean isExit;


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

    public static BeaconDto fromEntity(Beacon beacon) {
        return BeaconDto.builder()
                .beaconId(beacon.getId())
                .mapId(beacon.getMap().getId())
                .beaconCode(beacon.getBeaconCode())
                .name(beacon.getName())
                .coordX(beacon.getCoordX())
                .coordY(beacon.getCoordY())
                .isExit(beacon.getIsExit())
                .isCctv(beacon.getIsCctv())
                .cctvIp(beacon.getCctvIp())
                .build();
    }
}