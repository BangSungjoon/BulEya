package com.ssafy.jangan_backend.firelog.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SingleRoute {
	private int beaconId;
	private List<Integer> route;
}
