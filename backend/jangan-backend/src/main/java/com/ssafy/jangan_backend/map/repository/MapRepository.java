package com.ssafy.jangan_backend.map.repository;

import com.ssafy.jangan_backend.map.entity.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MapRepository extends JpaRepository<Map, Integer> {
    List<Map> findByStationId(Integer stationId);
}
