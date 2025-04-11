package com.ssafy.jangan_backend.station.repository;

import com.ssafy.jangan_backend.station.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StationRepository extends JpaRepository<Station, Integer> {
    Optional<Station> findById(int stationId);
}
