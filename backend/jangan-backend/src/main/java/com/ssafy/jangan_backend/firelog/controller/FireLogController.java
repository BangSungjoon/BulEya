package com.ssafy.jangan_backend.firelog.controller;

import com.ssafy.jangan_backend.common.response.BaseResponse;
import com.ssafy.jangan_backend.firelog.dto.FireImageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.jangan_backend.firelog.dto.FireReportDto;
import com.ssafy.jangan_backend.firelog.service.FirelogService;

import lombok.RequiredArgsConstructor;

@Tag(name = "화재 기록")
@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class FireLogController {
	private final FirelogService firelogService;

	@Operation(
			summary = "화재 상태 기록",
			description = "화재 감지 시 로그 기록/ 화재 감지 시 알림/ 화재 감지 시 최단경로 계산"
	)
	@PostMapping("/fire-report")
	public BaseResponse reportFire(@RequestPart("json") FireReportDto fireReportDto,
								   @RequestPart("files") MultipartFile[] files){
		firelogService.reportFire(fireReportDto, files);
		return BaseResponse.ok();
	}

	@Operation(
			summary = "화재 이미지 조회",
			description = "특정 지하철 비콘의 최근 화재 이미지 조회"
	)
	@GetMapping("cctv-image")
	public BaseResponse<FireImageDto> getCctvImage(@RequestParam("station_id") int stationId, @RequestParam("beacon_code") int beaconCode ) {
		FireImageDto fireImageDto = firelogService.getFireImageDto(stationId, beaconCode);
		return BaseResponse.ok(fireImageDto);
	}
}
