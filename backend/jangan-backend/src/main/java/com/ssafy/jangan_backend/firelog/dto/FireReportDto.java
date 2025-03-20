package com.ssafy.jangan_backend.firelog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FireReportDto {
	@JsonProperty("station_id")
	private int stationId;

	@JsonProperty("beacon_list")
	BeaconFireInfoDto[] beaconList;
}
