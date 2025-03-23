package com.ssafy.jangan_backend.firelog.dto;

import java.util.ArrayList;
import java.util.List;

import com.ssafy.jangan_backend.beacon.dto.BeaconNotificationDto;

import lombok.Getter;
import lombok.Setter;

// 화재 발생 시 모바일 앱으로 전송되는 데이터 DTO
@Getter
@Setter
public class FireNotificationDto {
	private String stationName;
	private Integer stationId;
	private List<BeaconNotificationDto> beaconNotificationDtos;
	public FireNotificationDto(){
		beaconNotificationDtos = new ArrayList<>();
	}
}
