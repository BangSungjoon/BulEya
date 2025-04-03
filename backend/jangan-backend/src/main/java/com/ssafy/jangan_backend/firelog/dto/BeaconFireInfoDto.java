package com.ssafy.jangan_backend.firelog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BeaconFireInfoDto {
	@JsonProperty("beacon_code")
	private int beaconCode;
	@JsonProperty("is_active_fire")
	private int isActiveFire;
}
