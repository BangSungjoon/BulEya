package com.ssafy.jangan_backend.edge.service;

import com.ssafy.jangan_backend.beacon.entity.Beacon;
import com.ssafy.jangan_backend.beacon.repository.BeaconRepository;
import com.ssafy.jangan_backend.edge.dto.EdgeDto;
import com.ssafy.jangan_backend.edge.entity.Edge;
import com.ssafy.jangan_backend.edge.repository.EdgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EdgeService {
    private final EdgeRepository edgeRepository;
    private final BeaconRepository beaconRepository;

    public EdgeDto saveEdge(EdgeDto edgeDto) {
        // 비콘 A 찾기
        Integer beaconACode = edgeDto.getBeaconACode();
        Beacon beaconA = beaconRepository.findByBeaconCode(beaconACode)
                .orElseThrow(() -> new IllegalArgumentException("비콘 A가 존재하지 않음"));
        // 비콘 B 찾기
        Integer beaconBCode = edgeDto.getBeaconBCode();
        Beacon beaconB = beaconRepository.findByBeaconCode(beaconBCode)
                .orElseThrow(() -> new IllegalArgumentException("비콘 B가 존재하지 않음"));

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
}
