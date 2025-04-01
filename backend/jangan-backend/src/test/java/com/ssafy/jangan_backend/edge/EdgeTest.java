package com.ssafy.jangan_backend.edge;

import com.ssafy.jangan_backend.beacon.entity.Beacon;
import com.ssafy.jangan_backend.beacon.repository.BeaconRepository;
import com.ssafy.jangan_backend.edge.dto.request.RequestRegisterEdgeDto;
import com.ssafy.jangan_backend.edge.dto.response.ResponseEdgeIdDto;
import com.ssafy.jangan_backend.edge.repository.EdgeQueryRepository;
import com.ssafy.jangan_backend.edge.repository.EdgeRepository;
import com.ssafy.jangan_backend.edge.service.EdgeService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EdgeTest {
    @Autowired
    private EdgeRepository edgeRepository;
    @Autowired
    private EdgeService edgeService;
    @Autowired
    private EdgeQueryRepository edgeQueryRepository;
    @Autowired
    private BeaconRepository beaconRepository;

    @Test
    @Transactional
    @Rollback
    @DisplayName("간선 등록 테스트")
    public void registerEdgeTest() {
        //GIVEN
        Beacon beaconA = beaconRepository.findAll().get(0);
        Beacon beaconB = beaconRepository.findAll().get(1);
        RequestRegisterEdgeDto testDto = RequestRegisterEdgeDto.builder()
                .stationId(222)
                .beaconACode(beaconA.getId())
                .beaconBCode(beaconB.getId())
                .build();
        //WHEN
        ResponseEdgeIdDto responseDto = edgeService.saveEdge(testDto);
        //THEN
        assertFalse(responseDto.getEdgeId()==null, "저장된 엔티티가 없습니다.");
    }
}
