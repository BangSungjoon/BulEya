package com.ssafy.jangan_backend.firelog.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.jangan_backend.common.exception.NotFoundException;
import com.ssafy.jangan_backend.common.response.BaseResponseStatus;
import com.ssafy.jangan_backend.firelog.dto.FireReportDto;
import com.ssafy.jangan_backend.station.entity.Station;
import com.ssafy.jangan_backend.station.repository.StationRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FirelogService {
	private StationRepository stationRepository;

	public int reportFire(FireReportDto fireReportDto, MultipartFile[] files) throws NotFoundException {
		int stationId = fireReportDto.getStationId();
		Optional<Station> station = stationRepository.findById(stationId);
		if(station.isEmpty())
			throw new NotFoundException(BaseResponseStatus.STATION_NOT_FOUND_EXCEPTION);

	}
}
