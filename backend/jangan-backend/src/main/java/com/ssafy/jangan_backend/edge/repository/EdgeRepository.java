package com.ssafy.jangan_backend.edge.repository;

import com.ssafy.jangan_backend.edge.entity.Edge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EdgeRepository extends JpaRepository<Edge, Integer> {
	List<Edge> findAllbyBeaconAIdIn(List<Integer> beaconIdList);
//    Edge findByStationIdAndFloor(int stationId, int floor);

    List<Edge> findByBeaconAIdOrBeaconBId(int beaconAId, int beaconBId); //두개의 값을 비교해서 에러가 발생할 수 있음
}
