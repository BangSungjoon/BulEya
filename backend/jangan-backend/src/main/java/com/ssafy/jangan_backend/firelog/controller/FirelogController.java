package com.ssafy.jangan_backend.firelog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.jangan_backend.firelog.dto.FireReportDto;
import com.ssafy.jangan_backend.firelog.service.FirelogService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class FirelogController {
	private final FirelogService firelogService;
	@PostMapping("/fire-report")
	public ResponseEntity<?> reportFire(@RequestPart("fireReportDto") FireReportDto fireReportDto, @RequestPart("files") MultipartFile[] files){
		firelogService.reportFire(fireReportDto, files);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
