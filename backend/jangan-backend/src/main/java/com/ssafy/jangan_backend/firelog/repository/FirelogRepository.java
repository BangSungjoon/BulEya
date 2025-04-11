package com.ssafy.jangan_backend.firelog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.jangan_backend.firelog.entity.FireLog;

@Repository
public interface FirelogRepository extends JpaRepository<FireLog, Integer> {
	Optional<FireLog> findFirstByBeaconIdOrderByCreatedAtDesc(Integer beaconId);
}
