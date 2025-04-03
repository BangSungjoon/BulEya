package com.ssafy.jangan_backend.beacon.service;

import com.ssafy.jangan_backend.beacon.dto.BeaconDto;
import com.ssafy.jangan_backend.beacon.dto.request.RequestDeleteBeaconDto;
import com.ssafy.jangan_backend.beacon.dto.request.RequestRegisterBeaconDto;
import com.ssafy.jangan_backend.beacon.dto.response.ResponseBeaconIdDto;
import com.ssafy.jangan_backend.beacon.dto.response.ResponseCctvInfoDto;
import com.ssafy.jangan_backend.beacon.dto.response.ResponseExitBeaconDto;
import com.ssafy.jangan_backend.beacon.entity.Beacon;
import com.ssafy.jangan_backend.beacon.repository.BeaconQueryRepository;
import com.ssafy.jangan_backend.beacon.repository.BeaconRepository;
import com.ssafy.jangan_backend.common.exception.CustomIllegalArgumentException;
import com.ssafy.jangan_backend.common.response.BaseResponseStatus;
import com.ssafy.jangan_backend.edge.repository.EdgeQueryRepository;
import com.ssafy.jangan_backend.edge.repository.EdgeRepository;
import com.ssafy.jangan_backend.map.entity.Map;
import com.ssafy.jangan_backend.map.repository.MapRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BeaconService {
    private final BeaconRepository beaconRepository;
    private final BeaconQueryRepository beaconQueryRepository;
    private final MapRepository mapRepository;
    private final EdgeQueryRepository edgeQueryRepository;
    private final EdgeRepository edgeRepository;

    @Transactional
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

    @Transactional
    public void deleteBeacon(RequestDeleteBeaconDto dto) {
        Integer beaconId = dto.getBeaconId();
        Beacon deletedBeacon = beaconRepository.findById(beaconId)
                .orElseThrow(() -> new CustomIllegalArgumentException(BaseResponseStatus.BEACON_NOT_FOUND_EXCEPTION));
        //연관 간선 삭제
        edgeRepository.deleteByBeaconAIdOrBeaconBId(deletedBeacon.getId(), deletedBeacon.getId());
        //비콘 삭제
        beaconRepository.deleteById(deletedBeacon.getId());
    }

    public List<ResponseCctvInfoDto> getCctvBeacon(Integer stationId) {
        List<Map> mapList = mapRepository.findByStationId(stationId);
        if (mapList.isEmpty()) {
            throw new CustomIllegalArgumentException(BaseResponseStatus.MAP_NOT_FOUND_EXCEPTION);
        }
        List<Integer> mapIdList = mapList.stream()
                .map(Map::getId)
                .toList();

        List<Beacon> beaconList = beaconRepository.findAllByMapIdIn(mapIdList);
        System.out.println(beaconList);
        if (beaconList.isEmpty()) {
            throw new CustomIllegalArgumentException(BaseResponseStatus.BEACON_NOT_FOUND_EXCEPTION);
        }
        return beaconList.stream()
                .filter(Beacon::getIsCctv)
                .map(beacon -> ResponseCctvInfoDto.builder()
                        .beaconCode(beacon.getBeaconCode())
                        .rtspUrl("rtsp://" + beacon.getCctvIp() + ":554/cctv")
                        .build())
                .toList();
    }

    public List<ResponseExitBeaconDto> getExitBeaconList(Integer stationId) {
        List<ResponseExitBeaconDto> allExitBeacon = beaconQueryRepository.findByIsExitAndMapIds(stationId);
        return allExitBeacon;
    }

    public BeaconDto getBeaconInfo(Integer stationId, Integer beaconCode){
        List<Integer> mapIds = mapRepository.findByStationId(stationId).stream().map(Map::getId).toList();
        Beacon beacon = beaconRepository.findByMapIdInAndBeaconCode(mapIds, beaconCode).orElseThrow(
            () -> new CustomIllegalArgumentException(BaseResponseStatus.MAP_NOT_FOUND_EXCEPTION));
        Optional<Map> mapOptional = mapRepository.findById(beacon.getMapId());
        Map map = mapOptional.get();
        return BeaconDto.fromEntity(beacon, map.getFloor());
    }
}
