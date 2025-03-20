package com.ssafy.jangan_backend.firelog.entity;

import java.util.Map;

import jakarta.persistence.Id;

import org.springframework.data.redis.core.RedisHash;

import com.ssafy.jangan_backend.firelog.dto.SingleRoute;

import lombok.Getter;

@Getter
@RedisHash("escapeRoute")
public class EscapeRoute {
	@Id
	private int stationId;
	private Map<Integer, SingleRoute> routes;
}
