package com.ssafy.jangan_backend.escapeRoute.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.ssafy.jangan_backend.escapeRoute.dto.RouteNodeDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Redis에 저장될 최단 경로 객체
 * 하나의 역에 포함된 모든 beacon에서의 최단 경로를 담는다.
 */
@Getter
@Setter
@RedisHash("escapeRoute")
@ToString
@AllArgsConstructor
public class EscapeRoute {
	@Id
	private int stationId;

	/**
	 * Key : 시작점 beaconCode
	 * Value : 이동 경로 beaconId(역방향)
	 */
	private final Map<Integer, List<RouteNodeDto>> routes;

	public EscapeRoute(){
		routes = new HashMap<>();
	}
}
