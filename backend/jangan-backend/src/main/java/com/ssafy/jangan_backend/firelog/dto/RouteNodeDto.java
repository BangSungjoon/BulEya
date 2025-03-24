package com.ssafy.jangan_backend.firelog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RouteNodeDto {
	@JsonProperty("beacon_code")
	private int beaconCode;
	@JsonProperty("floor")
	private int floor;
	@JsonProperty("coord_x")
	private int coordX;
	@JsonProperty("coord_y")
	private int coordY;
}
