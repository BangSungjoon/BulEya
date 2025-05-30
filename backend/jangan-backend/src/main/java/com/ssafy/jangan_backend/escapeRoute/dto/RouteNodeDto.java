package com.ssafy.jangan_backend.escapeRoute.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RouteNodeDto {
	@JsonProperty("beacon_code")
	private int beaconCode;
	@JsonProperty("floor")
	private int floor;
	@JsonProperty("coord_x")
	private double coordX;
	@JsonProperty("coord_y")
	private double coordY;
}
