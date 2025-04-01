package com.ssafy.jangan_backend.edge.repository;

import com.ssafy.jangan_backend.edge.entity.Edge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EdgeRepository extends JpaRepository<Edge, Integer> {
	List<Edge> findByBeaconAIdIn(List<Integer> beaconIdList);
    Boolean existsByBeaconAIdAndBeaconBId(int beaconAId, int BeaconBId);

    void deleteByBeaconAIdOrBeaconBId(Integer beaconAId, Integer beaconBId);
}
