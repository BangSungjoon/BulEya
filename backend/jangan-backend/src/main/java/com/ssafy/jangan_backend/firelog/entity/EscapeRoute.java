package com.ssafy.jangan_backend.firelog.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Id;

import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;
import lombok.Setter;

/**
 * Redis에 저장될 최단 경로 객체
 * 하나의 역에 포함된 모든 beacon에서의 최단 경로를 담는다.
 */
@Getter
@Setter
@RedisHash("escapeRoute")
public class EscapeRoute {
	@Id
	private int stationId;

	/**
	 * Key : 시작점 beaconCode
	 * Value : 이동 경로 beaconId(역방향)
	 */
	private final Map<Integer, List<Integer>> routes;

	public EscapeRoute(){
		routes = new HashMap<>();
	}
}
