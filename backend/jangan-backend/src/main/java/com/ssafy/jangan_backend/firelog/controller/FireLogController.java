package com.ssafy.jangan_backend.firelog.controller;

import com.ssafy.jangan_backend.common.response.BaseResponse;
import com.ssafy.jangan_backend.firelog.dto.FireImageDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.jangan_backend.firelog.dto.FireReportDto;
import com.ssafy.jangan_backend.firelog.service.FirelogService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class FireLogController {
	private final FirelogService firelogService;

	@PostMapping("/fire-report")
	public ResponseEntity<?> reportFire(@RequestPart("fireReportDto") FireReportDto fireReportDto, @RequestPart("files") MultipartFile[] files){
		firelogService.reportFire(fireReportDto, files);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@GetMapping("cctv-image")
	public BaseResponse<FireImageDto> getCctvImage(@RequestParam("station_id") int stationId, @RequestParam("beacon_code") int beaconCode ) {
		FireImageDto fireImageDto = firelogService.getFireImageDto(stationId, beaconCode);
		return BaseResponse.ok(fireImageDto);
	}
}
