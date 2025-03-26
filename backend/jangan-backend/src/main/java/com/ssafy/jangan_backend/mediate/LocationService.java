package com.ssafy.jangan_backend.mediate;

import com.ssafy.jangan_backend.beacon.dto.BeaconDto;
import com.ssafy.jangan_backend.beacon.repository.BeaconQueryRepository;
import com.ssafy.jangan_backend.common.util.MinioUtil;
import com.ssafy.jangan_backend.edge.dto.EdgeDto;
import com.ssafy.jangan_backend.edge.repository.EdgeQueryRepository;
import com.ssafy.jangan_backend.map.dto.ResponseWebAdminMapDto;
import com.ssafy.jangan_backend.map.entity.Map;
import com.ssafy.jangan_backend.map.repository.MapQueryRepository;
import com.ssafy.jangan_backend.map.repository.MapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final MapRepository mapRepository;
    private final MinioUtil minioUtil;
    private final MapQueryRepository mapQueryRepository;
    private final BeaconQueryRepository beaconQueryRepository;
    private final EdgeQueryRepository edgeQueryRepository;

    public List<ResponseWebAdminMapDto> getMapsForWebAdmin(Integer stationId) {
        List<ResponseWebAdminMapDto> dtoList = new ArrayList<>();
        List<Map> mapList = mapRepository.findByStationId(stationId); //조회하려는 역 아이디의 평면도 전체 조회
        //TODO : 비콘조회를 Map수만큼 하지말고, mapId를 추출 후 한번에 조회하도록 바꾸기
        for(Map map : mapList) {
            String imageURL = minioUtil.getPresignedUrl(map.getBucketName(), map.getImageName()); //평면도의 url
            //한 층에 해당하는 비콘들 조회
            List<BeaconDto>  beaconDtoList = beaconQueryRepository.findByMapId(map.getId());
            //비콘Id 추출
            List<Integer> beaconIds = beaconDtoList.stream().map(BeaconDto::getBeaconId).toList();
            //비콘Id에 해당하는 간선들 조회
            List<EdgeDto> edgeDtoList = edgeQueryRepository.findByBeaconIds(beaconIds);

            dtoList.add(ResponseWebAdminMapDto.builder()
                    .floor(map.getFloor())
                    .imageUrl(imageURL)
                    .beaconList(beaconDtoList)
                    .edgeList(edgeDtoList)
                    .build());
        }
        return dtoList;
    }
}
