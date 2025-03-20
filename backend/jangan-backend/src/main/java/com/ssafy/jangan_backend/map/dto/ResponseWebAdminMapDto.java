package com.ssafy.jangan_backend.map.dto;

import com.ssafy.jangan_backend.beacon.dto.BeaconDto;
import com.ssafy.jangan_backend.edge.dto.EdgeDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseWebAdminMapDto {
    private Integer floor;
    private String imageUrl;
    private List<BeaconDto> beaconList;
    private List<EdgeDto> edgeList;
}
