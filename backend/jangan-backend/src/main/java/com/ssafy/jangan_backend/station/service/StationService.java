package com.ssafy.jangan_backend.station.service;

import com.ssafy.jangan_backend.common.exception.NotFoundException;
import com.ssafy.jangan_backend.common.exception.UnauthorizedAccessException;
import com.ssafy.jangan_backend.common.response.BaseResponseStatus;
import com.ssafy.jangan_backend.station.dto.RequestAdminLoginDto;
import com.ssafy.jangan_backend.station.entity.Station;
import com.ssafy.jangan_backend.station.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StationService {
    private final StationRepository stationRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    public Station findByIdOrElseThrows(int stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new NotFoundException(BaseResponseStatus.STATION_NOT_FOUND_EXCEPTION));
    }

    public boolean adminLogin(RequestAdminLoginDto loginDto) {
        Station station = findByIdOrElseThrows(loginDto.getStationId());
        String accessKey = loginDto.getAccessKey();
        if(!passwordEncoder.matches(accessKey,station.getAccessKey())) {
            throw new UnauthorizedAccessException(BaseResponseStatus.AUTHENTICATION_FAILED_EXCEPTION);
        }
        return true;
    }
}
