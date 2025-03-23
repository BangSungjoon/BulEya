package com.ssafy.jangan_backend.edge.repository;

import java.util.List;

import com.ssafy.jangan_backend.edge.entity.Edge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EdgeRepository extends JpaRepository<Edge, Integer> {
	List<Edge> findAllbyBeaconAIdIn(List<Integer> beaconIdList);
}
