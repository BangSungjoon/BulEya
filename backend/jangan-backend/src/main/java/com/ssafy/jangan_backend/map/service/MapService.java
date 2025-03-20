package com.ssafy.jangan_backend.map.service;

import com.ssafy.jangan_backend.common.util.MinioUtil;
import com.ssafy.jangan_backend.map.dto.ResponseMapDto;
import com.ssafy.jangan_backend.map.entity.Map;
import com.ssafy.jangan_backend.map.repository.MapRepository;
import com.ssafy.jangan_backend.station.entity.Station;
import com.ssafy.jangan_backend.station.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MapService {
    private final MapRepository mapRepository;
    private final StationService stationService;
    private final MinioUtil minioUtil;
    public List<ResponseMapDto.Mobile> getMapsForMobile(Integer stationId) {
        Station station = stationService.findByIdOrElseThrows(stationId);

        //TODO : QueryDSL로 변환하기
        List<Map> mapList = mapRepository.findByStationId(station.getId());
        List<ResponseMapDto.Mobile> mapUrlList = mapList.stream()
                .map(map -> ResponseMapDto.Mobile
                        .builder()
                        .floor(map.getFloor())
                        .imageUrl(minioUtil.getPresignedUrl(map.getBucketName(), map.getImageName()))
                        .build()
                ).toList();
        return mapUrlList;
    }
}
