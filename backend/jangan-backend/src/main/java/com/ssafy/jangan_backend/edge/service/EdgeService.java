package com.ssafy.jangan_backend.edge.service;

import com.ssafy.jangan_backend.beacon.dto.BeaconDto;
import com.ssafy.jangan_backend.beacon.entity.Beacon;
import com.ssafy.jangan_backend.beacon.repository.BeaconRepository;
import com.ssafy.jangan_backend.common.exception.CustomIllegalArgumentException;
import com.ssafy.jangan_backend.common.exception.DuplicateDataException;
import com.ssafy.jangan_backend.common.response.BaseResponseStatus;
import com.ssafy.jangan_backend.edge.dto.EdgeDto;
import com.ssafy.jangan_backend.edge.dto.request.RequestDeleteEdgeDto;
import com.ssafy.jangan_backend.edge.dto.request.RequestRegisterEdgeDto;
import com.ssafy.jangan_backend.edge.dto.response.ResponseEdgeIdDto;
import com.ssafy.jangan_backend.edge.entity.Edge;
import com.ssafy.jangan_backend.edge.repository.EdgeQueryRepository;
import com.ssafy.jangan_backend.edge.repository.EdgeRepository;
import com.ssafy.jangan_backend.map.entity.Map;
import com.ssafy.jangan_backend.map.repository.MapRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EdgeService {
    private final EdgeRepository edgeRepository;
    private final EdgeQueryRepository edgeQueryRepository;
    private final BeaconRepository beaconRepository;
    private final MapRepository mapRepository;

    @Transactional
    public ResponseEdgeIdDto saveEdge(RequestRegisterEdgeDto dto) {
        Integer stationId = dto.getStationId();
        Integer beaconACode = dto.getBeaconACode();
        Integer beaconBCode = dto.getBeaconBCode();

        // station의 모든 map의 Id를 리스트로 변환
        List<Integer> mapIdList = mapRepository.findByStationId(stationId)
                .stream()
                .map(Map::getId)
                .toList();

        // beaconA와 beaconB 찾기
        Beacon beaconA = beaconRepository.findByMapIdInAndBeaconCode(mapIdList, beaconACode)
                .orElseThrow(() -> new CustomIllegalArgumentException(BaseResponseStatus.BEACON_NOT_FOUND_EXCEPTION));
        Beacon beaconB = beaconRepository.findByMapIdInAndBeaconCode(mapIdList, beaconBCode)
                .orElseThrow(() -> new CustomIllegalArgumentException(BaseResponseStatus.BEACON_NOT_FOUND_EXCEPTION));

        //같은 간선이 이미 있는지 확인
        if(edgeRepository.existsByBeaconAIdAndBeaconBId(beaconA.getId(), beaconB.getId())) {
            throw new DuplicateDataException(BaseResponseStatus.EDGE_ALREADY_EXISTS_EXCEPTION);
        }
        // Entity 변환 후 저장
        Edge newEdge = dto.toEntity(beaconA, beaconB);
        Edge newReverseEdge = newEdge.reverseEdge();
        edgeRepository.save(newEdge);
        edgeRepository.save(newReverseEdge);

        // ResponseEdgeIdDto 반환
        ResponseEdgeIdDto responseEdgeIdDto = ResponseEdgeIdDto.toDto(newEdge);
        return responseEdgeIdDto;
    }

    @Transactional
    public void deleteEdge(RequestDeleteEdgeDto dto) {
        Integer edgeId = dto.getEdgeId();
        Edge deletedEdge = edgeRepository.findById(edgeId)
                .orElseThrow(() -> new CustomIllegalArgumentException(BaseResponseStatus.EDGE_NOT_FOUND_EXCEPTION));
        edgeQueryRepository.deleteEdge(deletedEdge);
    }
}
