package com.ssafy.jangan_backend.map.service;

import com.ssafy.jangan_backend.beacon.dto.BeaconDto;
import com.ssafy.jangan_backend.beacon.entity.Beacon;
import com.ssafy.jangan_backend.beacon.repository.BeaconRepository;
import com.ssafy.jangan_backend.common.util.MinioUtil;
import com.ssafy.jangan_backend.edge.dto.EdgeDto;
import com.ssafy.jangan_backend.edge.entity.Edge;
import com.ssafy.jangan_backend.edge.repository.EdgeRepository;
import com.ssafy.jangan_backend.map.dto.MapDto;
import com.ssafy.jangan_backend.map.dto.ResponseMobileMapDto;
import com.ssafy.jangan_backend.map.dto.ResponseWebAdminMapDto;
import com.ssafy.jangan_backend.map.entity.Map;
import com.ssafy.jangan_backend.map.repository.MapRepository;
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
    private final MinioUtil minioUtil;
    private final EdgeRepository edgeRepository;
    private final BeaconRepository beaconRepository;

    public List<ResponseMobileMapDto> getMapsForMobile(Integer stationId) {
        Station station = stationService.findByIdOrElseThrows(stationId);

        //TODO : QueryDSL로 변환하기
        List<Map> mapList = mapRepository.findByStationId(station.getId());
        List<ResponseMobileMapDto> mapUrlList = mapList.stream()
                .map(map -> ResponseMobileMapDto
                        .builder()
                        .floor(map.getFloor())
                        .imageUrl(minioUtil.getPresignedUrl(map.getBucketName(), map.getImageName()))
                        .build()
                ).toList();
        return mapUrlList;
    }

    //TODO : QueryDSL 적용하기 전과 적용한 후 성능차이 확인하기
    public List<ResponseWebAdminMapDto> getMapsForWebAdmin(int stationId) {
        Station station = stationService.findByIdOrElseThrows(stationId);

        List<ResponseWebAdminMapDto> dtoList = new ArrayList<>(); //반환할 dtoList
        List<Map> mapList = mapRepository.findByStationId(station.getId()); //조회하려는 역 아이디의 평면도 전체 조회

        for(Map map : mapList) {
            String imageURL = minioUtil.getPresignedUrl(map.getBucketName(), map.getImageName()); //평면도의 url
            List<Beacon> beaconList = beaconRepository.findByMapId(map.getId()); //맵에 해당하는 비콘 가져오기
            //한 층에 해당하는 비콘들
            List<BeaconDto>  floorBeaconList = beaconList.stream()
                    .map(beacon -> BeaconDto.fromEntity(beacon))
                    .toList();

            //비콘에 해당하는 간선 가져오기
            List<Edge> edgeList = new ArrayList<>();
            for(Beacon beacon : beaconList) {
                edgeList = edgeRepository.findByBeaconAIdOrBeaconBId(beacon.getId(),beacon.getId());
            }

            //edgeList -> floorEdgeList로 변환
            List<EdgeDto> floorEdgeList = edgeList.stream()
                    .map(edge -> EdgeDto.fromEntity(edge))
                    .toList();

            dtoList.add(ResponseWebAdminMapDto.builder()
                    .floor(map.getFloor())
                    .imageUrl(imageURL)
                    .edgeList(floorEdgeList)
                    .beaconList(floorBeaconList)
                    .build());
        }
        return dtoList;
    }

    public List<MapDto> getMapListByStationId(Integer stationId) {
        List<MapDto> mapList = mapRepository.findByStationId(stationId)
                .stream()
                .map(map -> MapDto.fromEntity(map))
                .toList();
        return mapList;
    }
}
