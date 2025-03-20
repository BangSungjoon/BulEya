package com.ssafy.jangan_backend.edge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EdgeDto {
    private Integer edgeId;
    @JsonProperty("beacon_a_code")
    private Integer beaconAcode;
    @JsonProperty("beacon_b_code")
    private Integer beaconBcode;
    private Integer distance;
}
