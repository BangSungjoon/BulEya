package com.ssafy.jangan_backend.firelog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.jangan_backend.firelog.dto.BeaconFireInfoDto;

@RequestMapping("/api")
@RestController
public class FirelogController {
	@PostMapping("/fire-report")
	public ResponseEntity<?> reportFire(BeaconFireInfoDto beaconFireInfoDto, MultipartFile[] files){


		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
