package com.ssafy.jangan_backend.beacon.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssafy.jangan_backend.beacon.dto.BeaconDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseExitBeaconDto {
    private Integer floor;
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

}
