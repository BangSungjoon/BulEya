package com.ssafy.jangan_backend.edge.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssafy.jangan_backend.beacon.entity.Beacon;
import com.ssafy.jangan_backend.edge.entity.Edge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RequestRegisterEdgeDto {
    private Integer stationId;
    private Integer floor;

    @JsonProperty("beacon_a_code")
    private Integer beaconACode;

    @JsonProperty("beacon_b_code")
    private Integer beaconBCode;

    private Integer distance;

    public Edge toEntity(Beacon beaconA, Beacon beaconB) {
        return Edge.builder()
                .beaconAId(beaconA.getId())
                .beaconBId(beaconB.getId())
                .distance(this.distance)
                .build();
    }
}
