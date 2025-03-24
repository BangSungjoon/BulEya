package com.ssafy.jangan_backend.beacon.service;

import org.springframework.stereotype.Service;

import com.ssafy.jangan_backend.beacon.dto.request.RequestRegisterBeaconDto;
import com.ssafy.jangan_backend.beacon.dto.response.ResponsBeaconIdDto;
import com.ssafy.jangan_backend.beacon.entity.Beacon;
import com.ssafy.jangan_backend.beacon.repository.BeaconRepository;
import com.ssafy.jangan_backend.common.exception.CustomIllegalArgumentException;
import com.ssafy.jangan_backend.common.response.BaseResponseStatus;
import com.ssafy.jangan_backend.map.entity.Map;
import com.ssafy.jangan_backend.map.repository.MapRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BeaconService {
    private final BeaconRepository beaconRepository;
    private final MapRepository mapRepository;

    public ResponsBeaconIdDto saveBeacon(RequestRegisterBeaconDto dto) {
        // staionId와 floor로 mapId 찾기
        Map map = mapRepository.findByStationIdAndFloor(dto.getStationId(), dto.getFloor())
                .orElseThrow(() -> new CustomIllegalArgumentException(BaseResponseStatus.MAP_NOT_FOUND_EXCEPTION));

        // Beacon 엔티티 생성, 저장
        Beacon newBeacon = dto.toEntity(map);
        beaconRepository.save(newBeacon);

        // DTO 반환
        return ResponsBeaconIdDto.builder()
                .beaconId(newBeacon.getId())
                .build();
    }

    public void deleteBeacon(Integer beaconId) {
        if(beaconRepository.existsById(beaconId)) {
            throw new IllegalArgumentException("해당 비콘 없음");
        }
        beaconRepository.deleteById(beaconId);
    }
}
