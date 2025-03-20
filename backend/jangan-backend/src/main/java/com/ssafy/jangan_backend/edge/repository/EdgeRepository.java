package com.ssafy.jangan_backend.edge.repository;

import com.ssafy.jangan_backend.edge.entity.Edge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EdgeRepository extends JpaRepository<Edge, Integer> {
    Edge findByStationIdAndF(int stationId, int floor);
}
