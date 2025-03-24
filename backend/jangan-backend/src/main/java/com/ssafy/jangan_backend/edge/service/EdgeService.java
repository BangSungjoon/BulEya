package com.ssafy.jangan_backend.edge.service;

import com.ssafy.jangan_backend.beacon.entity.Beacon;
import com.ssafy.jangan_backend.beacon.repository.BeaconRepository;
import com.ssafy.jangan_backend.common.exception.CustomIllegalArgumentException;
import com.ssafy.jangan_backend.common.response.BaseResponseStatus;
import com.ssafy.jangan_backend.edge.dto.request.RequestDeleteEdgeDto;
import com.ssafy.jangan_backend.edge.dto.request.RequestRegisterEdgeDto;
import com.ssafy.jangan_backend.edge.dto.response.ResponseEdgeIdDto;
import com.ssafy.jangan_backend.edge.entity.Edge;
import com.ssafy.jangan_backend.edge.repository.EdgeRepository;
import com.ssafy.jangan_backend.map.entity.Map;
import com.ssafy.jangan_backend.map.repository.MapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EdgeService {
    private final EdgeRepository edgeRepository;
    private final BeaconRepository beaconRepository;
    private final MapRepository mapRepository;

    public ResponseEdgeIdDto saveEdge(RequestRegisterEdgeDto dto) {
        Integer stationId = dto.getStationId();
        Integer floor = dto.getFloor();

        // stationId와 floor로 mapId 찾기
        Map map = mapRepository.findByStationIdAndFloor(stationId, floor)
                .orElseThrow(() -> new CustomIllegalArgumentException(BaseResponseStatus.MAP_NOT_FOUND_EXCEPTION));
        Integer mapId = map.getId();

        // 비콘 A 찾기
        Integer beaconACode = dto.getBeaconACode();
        Beacon beaconA = beaconRepository.findByMapIdAndBeaconCode(mapId, beaconACode)
                .orElseThrow(() -> new CustomIllegalArgumentException(BaseResponseStatus.BEACON_NOT_FOUND_EXCEPTION));
        // 비콘 B 찾기
        Integer beaconBCode = dto.getBeaconBCode();
        Beacon beaconB = beaconRepository.findByMapIdAndBeaconCode(mapId, beaconBCode)
                .orElseThrow(() -> new CustomIllegalArgumentException(BaseResponseStatus.BEACON_NOT_FOUND_EXCEPTION));

        // Entity 변환 후 저장
        Edge newEdge = dto.toEntity(beaconA, beaconB);
        edgeRepository.save(newEdge);

        // ResponseEdgeIdDto 반환
        ResponseEdgeIdDto responseEdgeIdDto = ResponseEdgeIdDto.toDto(newEdge);
        return responseEdgeIdDto;
    }

    public void deleteEdge(RequestDeleteEdgeDto dto) {
        Integer edgeId = dto.getEdgeId();
        try {
            edgeRepository.deleteById(edgeId);
        } catch (EmptyResultDataAccessException e) {
            throw new CustomIllegalArgumentException(BaseResponseStatus.EDGE_NOT_FOUND_EXCEPTION);
        }
    }
}
