package com.ssafy.jangan_backend.fcm.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ssafy.jangan_backend.fcm.entity.FcmToken;
import com.ssafy.jangan_backend.fcm.repository.FcmTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FcmTokenService {
	private final FcmTokenRepository fcmTokenRepository;

	public void refreshToken(String uuid, String token){
		Optional<FcmToken> fcmTokenOptional = fcmTokenRepository.findByUuid(uuid);
		FcmToken newToken = fcmTokenOptional.orElseGet(() -> new FcmToken(uuid, token));
		newToken.setToken(token);
		fcmTokenRepository.save(newToken);
	}

}
