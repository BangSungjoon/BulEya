package com.ssafy.jangan_backend.edge.service;

import com.ssafy.jangan_backend.beacon.dto.BeaconDto;
import com.ssafy.jangan_backend.beacon.entity.Beacon;
import com.ssafy.jangan_backend.beacon.repository.BeaconRepository;
import com.ssafy.jangan_backend.common.exception.CustomIllegalArgumentException;
import com.ssafy.jangan_backend.common.response.BaseResponseStatus;
import com.ssafy.jangan_backend.edge.dto.EdgeDto;
import com.ssafy.jangan_backend.edge.entity.Edge;
import com.ssafy.jangan_backend.edge.repository.EdgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EdgeService {
    private final EdgeRepository edgeRepository;
    private final BeaconRepository beaconRepository;

    public EdgeDto saveEdge(EdgeDto edgeDto) {
        // 비콘 A 찾기
        Integer beaconACode = edgeDto.getBeaconACode();
        Beacon beaconA = beaconRepository.findByBeaconCode(beaconACode)
                .orElseThrow(() -> new CustomIllegalArgumentException(BaseResponseStatus.BEACON_NOT_FOUND_EXCEPTION));
        // 비콘 B 찾기
        Integer beaconBCode = edgeDto.getBeaconBCode();
        Beacon beaconB = beaconRepository.findByBeaconCode(beaconBCode)
                .orElseThrow(() -> new CustomIllegalArgumentException(BaseResponseStatus.BEACON_NOT_FOUND_EXCEPTION));

        // Entity 변환 후 저장
        Edge edge = edgeDto.toEntity(beaconA, beaconB);
        edgeRepository.save(edge);

        // DTO 반환
        return EdgeDto.toDto(edge);
    }

    public void deleteEdge(EdgeDto edgeDto) {
        Integer edgeId = edgeDto.getEdgeId();
        edgeRepository.deleteById(edgeId);
    }

    public List<EdgeDto> getEdgeList(Integer beaconId) {
        List<EdgeDto> edgeList = edgeRepository.findByBeaconAIdOrBeaconBId(beaconId, beaconId)
                .stream()
                .map(edge -> EdgeDto.fromEntity(edge))
                .toList();
        return edgeList;
    }
}
