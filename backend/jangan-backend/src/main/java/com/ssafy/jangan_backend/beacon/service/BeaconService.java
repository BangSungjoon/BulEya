package com.ssafy.jangan_backend.beacon.service;

import com.ssafy.jangan_backend.beacon.dto.BeaconDto;
import com.ssafy.jangan_backend.beacon.entity.Beacon;
import com.ssafy.jangan_backend.beacon.repository.BeaconRepository;
import com.ssafy.jangan_backend.common.exception.CustomIllegalArgumentException;
import com.ssafy.jangan_backend.common.response.BaseResponseStatus;
import com.ssafy.jangan_backend.map.entity.Map;
import com.ssafy.jangan_backend.map.repository.MapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BeaconService {
    private final BeaconRepository beaconRepository;
    private final MapRepository mapRepository;

    public BeaconDto saveBeacon(BeaconDto beaconDto) {
        // staionId와 floor로 mapId 찾기
        Map map = mapRepository.findByStationIdAndFloor(beaconDto.getStationId(), beaconDto.getFloor())
                .orElseThrow(() -> new CustomIllegalArgumentException(BaseResponseStatus.MAP_NOT_FOUND_EXCEPTION));

        // Beacon 엔티티 생성, 저장
        Beacon beacon = beaconDto.toEntity(map);
        beaconRepository.save(beacon);

        // DTO 반환
        return BeaconDto.toDto(beacon);
    }

    public void deleteBeacon(BeaconDto beaconDto) {
        Integer beaconId = beaconDto.getBeaconId();

        if (beaconId == null) {
            throw new IllegalArgumentException("해당 비콘 없음");
        }
        beaconRepository.deleteById(beaconId);
    }
}
