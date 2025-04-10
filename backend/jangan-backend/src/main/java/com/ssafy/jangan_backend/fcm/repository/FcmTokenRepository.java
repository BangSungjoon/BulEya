package com.ssafy.jangan_backend.fcm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.jangan_backend.fcm.entity.FcmToken;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Integer> {
	Optional<FcmToken> findByUuid(String uuid);
}
