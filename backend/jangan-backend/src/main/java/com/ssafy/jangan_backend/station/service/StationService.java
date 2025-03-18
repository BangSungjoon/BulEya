package com.ssafy.jangan_backend.station.service;

import com.ssafy.jangan_backend.common.exception.NotFoundException;
import com.ssafy.jangan_backend.common.response.BaseResponseStatus;
import com.ssafy.jangan_backend.station.entity.Station;
import com.ssafy.jangan_backend.station.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StationService {
    private final StationRepository stationRepository;

    public Station findByIdOrElseThrows(int stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new NotFoundException(BaseResponseStatus.STATION_NOT_FOUND_EXCEPTION));
    }
}
