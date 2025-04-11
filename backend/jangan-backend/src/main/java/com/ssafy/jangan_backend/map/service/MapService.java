package com.ssafy.jangan_backend.map.service;

import com.ssafy.jangan_backend.beacon.dto.BeaconDto;
import com.ssafy.jangan_backend.beacon.entity.Beacon;
import com.ssafy.jangan_backend.beacon.repository.BeaconRepository;
import com.ssafy.jangan_backend.beacon.service.BeaconService;
import com.ssafy.jangan_backend.common.util.MinioUtil;
import com.ssafy.jangan_backend.edge.dto.EdgeDto;
import com.ssafy.jangan_backend.edge.entity.Edge;
import com.ssafy.jangan_backend.edge.repository.EdgeRepository;
import com.ssafy.jangan_backend.edge.service.EdgeService;
import com.ssafy.jangan_backend.map.dto.MapDto;
import com.ssafy.jangan_backend.map.dto.ResponseMobileMapDto;
import com.ssafy.jangan_backend.map.dto.ResponseWebAdminMapDto;
import com.ssafy.jangan_backend.map.entity.Map;
import com.ssafy.jangan_backend.map.repository.MapRepository;
import com.ssafy.jangan_backend.mediate.LocationService;
import com.ssafy.jangan_backend.station.entity.Station;
import com.ssafy.jangan_backend.station.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MapService {
    private final MapRepository mapRepository;
    private final StationService stationService;
    private final LocationService locationService;
    private final MinioUtil minioUtil;
    private final BeaconRepository beaconRepository;

    public List<ResponseMobileMapDto> getMapsForMobile(Integer stationId) {
        Station station = stationService.findByIdOrElseThrows(stationId);

        List<Map> mapList = mapRepository.findByStationId(station.getId());
        //MapEntity Dto로 변환
        List<ResponseMobileMapDto> mapUrlList = mapList.stream()
                .map(map -> ResponseMobileMapDto.fromEntity(map, minioUtil.getPresignedUrl(map.getBucketName(), map.getImageName())))
                .toList();
        return mapUrlList;
    }

    public List<MapDto> getMapListByStationId(Integer stationId) {
        Station station = stationService.findByIdOrElseThrows(stationId);
        List<MapDto> mapList = mapRepository.findByStationId(station.getId())
                .stream()
                .map(map -> MapDto.fromEntity(map))
                .toList();
        return mapList;
    }

    public List<ResponseWebAdminMapDto> getMapsForWebAdmin(int stationId) {
        Station station = stationService.findByIdOrElseThrows(stationId);
        List<ResponseWebAdminMapDto> dtoList = locationService.getMapsForWebAdmin(station.getId());
        return dtoList;
    }
}