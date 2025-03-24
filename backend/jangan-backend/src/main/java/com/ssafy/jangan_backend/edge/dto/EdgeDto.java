package com.ssafy.jangan_backend.edge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssafy.jangan_backend.beacon.dto.BeaconDto;
import com.ssafy.jangan_backend.beacon.entity.Beacon;
import com.ssafy.jangan_backend.edge.entity.Edge;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class EdgeDto {
    private Integer edgeId;

    @JsonProperty("beacon_a_code")
    private Integer beaconACode;

    @JsonProperty("beacon_b_code")
    private Integer beaconBCode;

    private Integer distance;

    public Edge toEntity(Beacon beaconA, Beacon beaconB) {
        return Edge.builder()
                .beaconA(beaconA)
                .beaconB(beaconB)
                .distance(this.distance)
                .build();
    }

    public static EdgeDto toDto(Edge edge) {
        return EdgeDto.builder()
                .edgeId(edge.getId())
                .beaconACode(edge.getBeaconA().getId())
                .beaconBCode(edge.getBeaconB().getId())
                .distance(edge.getDistance())
                .build();
    }
}
