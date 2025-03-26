package com.ssafy.jangan_backend.escapeRoute.service;

import com.ssafy.jangan_backend.beacon.dto.BeaconDto;
import com.ssafy.jangan_backend.beacon.entity.Beacon;
import com.ssafy.jangan_backend.beacon.repository.BeaconQueryRepository;
import com.ssafy.jangan_backend.beacon.service.BeaconService;
import com.ssafy.jangan_backend.edge.dto.EdgeDto;
import com.ssafy.jangan_backend.edge.repository.EdgeQueryRepository;
import com.ssafy.jangan_backend.edge.service.EdgeService;
import com.ssafy.jangan_backend.escapeRoute.dto.EscapeRouteDto;
import com.ssafy.jangan_backend.firelog.entity.EscapeRoute;
import com.ssafy.jangan_backend.map.dto.MapDto;
import com.ssafy.jangan_backend.map.entity.Map;
import com.ssafy.jangan_backend.map.service.MapService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EscapeRouteService {
    private final MapService mapService;
    private final BeaconQueryRepository beaconQueryRepository;
    private final EdgeQueryRepository edgeQueryRepository;
    //TODO : 최단경로 알고리즘 구현하기
    public EscapeRouteDto getEscapeRoute(Integer stationId, Integer beaconCode) {
        List<MapDto> mapList = mapService.getMapListByStationId(stationId);
        List<BeaconDto> beaconList = getBeaconListByMapId(mapList);
        List<EdgeDto> edgeList = getEdgeListByBeaconId(beaconList);

        System.out.println("mapDto======================");
        for(MapDto dto : mapList) System.out.println(dto.toString());
        System.out.println("beaconDto======================");
        for(BeaconDto dto : beaconList) System.out.println(dto.toString());
        System.out.println("edgeDto======================");
        for(EdgeDto dto : edgeList) System.out.println(dto.toString());

        return null;
    }
    private List<BeaconDto> getBeaconListByMapId(List<MapDto> mapList) {
        List<Integer> mapIds = mapList.stream().map(MapDto::getId).toList();
        return beaconQueryRepository.findByMapIds(mapIds);
    }

    private List<EdgeDto> getEdgeListByBeaconId(List<BeaconDto> beaconList) {
        List<Integer> beaconIds = beaconList.stream().map(BeaconDto::getBeaconId).toList();
        return edgeQueryRepository.findByBeaconIds(beaconIds);
    }
}
