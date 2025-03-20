package com.ssafy.jangan_backend.beacon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeaconDto {
    private Integer id;
    private Integer beaconCode;
    private String name;
    private Integer coordX;
    private Integer coordY;
    private Boolean isExit;
    private Boolean isCctv;
    private String cctvIp;

}
