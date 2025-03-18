package com.ssafy.jangan_backend.edge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ssafy.jangan_backend.beacon.dto.BeaconDto;
import com.ssafy.jangan_backend.beacon.entity.Beacon;
import com.ssafy.jangan_backend.edge.entity.Edge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EdgeDto {
    private Integer edgeId;
    private Integer stationId;
    private Integer floor;
    private Integer beaconACode;
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
                .build();
    }
}
