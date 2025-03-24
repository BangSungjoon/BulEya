package com.ssafy.jangan_backend.beacon.service;

import com.ssafy.jangan_backend.beacon.dto.request.RequestDeleteBeaconDto;
import com.ssafy.jangan_backend.beacon.dto.request.RequestRegisterBeaconDto;
import com.ssafy.jangan_backend.beacon.dto.response.ResponseBeaconIdDto;
import com.ssafy.jangan_backend.beacon.entity.Beacon;
import com.ssafy.jangan_backend.beacon.repository.BeaconRepository;
import com.ssafy.jangan_backend.common.exception.CustomIllegalArgumentException;
import com.ssafy.jangan_backend.common.response.BaseResponseStatus;
import com.ssafy.jangan_backend.map.entity.Map;
import com.ssafy.jangan_backend.map.repository.MapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BeaconService {
    private final BeaconRepository beaconRepository;
    private final MapRepository mapRepository;

    public ResponseBeaconIdDto saveBeacon(RequestRegisterBeaconDto dto) {
        // staionId와 floor로 mapId 찾기
        Map map = mapRepository.findByStationIdAndFloor(dto.getStationId(), dto.getFloor())
                .orElseThrow(() -> new CustomIllegalArgumentException(BaseResponseStatus.MAP_NOT_FOUND_EXCEPTION));

        // Beacon 엔티티로 변환, 저장
        Beacon newBeacon = dto.toEntity(map);
        beaconRepository.save(newBeacon);

        // ResponseBeaconIdDto 반환
        ResponseBeaconIdDto responseBeaconIdDto = ResponseBeaconIdDto.toDto(newBeacon);
        return responseBeaconIdDto;
    }

    public void deleteBeacon(RequestDeleteBeaconDto dto) {
        Integer beaconId = dto.getBeaconId();
        try {
            beaconRepository.deleteById(beaconId);
        } catch (EmptyResultDataAccessException e) {
            throw new CustomIllegalArgumentException(BaseResponseStatus.BEACON_NOT_FOUND_EXCEPTION);
        }
    }
}
