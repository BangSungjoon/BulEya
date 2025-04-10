package com.ssafy.jangan_backend.fcm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.jangan_backend.fcm.service.FcmTokenService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController("/api")
public class FcmTokenController {
	private final FcmTokenService fcmTokenService;

	@PostMapping("/refresh-fcm-token")
	public ResponseEntity<?> refreshToken(@RequestParam("uuid") String uuid, @RequestParam("token") String token){
		fcmTokenService.refreshToken(uuid, token);
		return ResponseEntity.ok().build();
	}
}
