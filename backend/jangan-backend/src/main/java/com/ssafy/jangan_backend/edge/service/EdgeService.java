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

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EdgeService {
    private final EdgeRepository edgeRepository;
    private final BeaconRepository beaconRepository;
    private final MapRepository mapRepository;

    public ResponseEdgeIdDto saveEdge(RequestRegisterEdgeDto dto) {
        Integer stationId = dto.getStationId();
        Integer beaconACode = dto.getBeaconACode();
        Integer beaconBCode = dto.getBeaconBCode();

        // station의 모든 map의 Id를 리스트로 변환
        List<Integer> mapIdList = mapRepository.findByStationId(stationId).stream().map(Map::getId).toList();

        // beaconA와 beaconB 찾기
        Beacon beaconA = beaconRepository.findByMapIdInAndBeaconCode(mapIdList, beaconACode)
                .orElseThrow(() -> new CustomIllegalArgumentException(BaseResponseStatus.BEACON_NOT_FOUND_EXCEPTION));
        Beacon beaconB = beaconRepository.findByMapIdInAndBeaconCode(mapIdList, beaconBCode)
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
