package com.ssafy.jangan_backend.firelog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FireReportDto {
	@JsonProperty(value = "station_id", required = true)
	private int stationId;

	@JsonProperty(value = "beacon_list", required = true)
	BeaconFireInfoDto[] beaconFireInfoList;
}
