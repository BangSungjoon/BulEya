package com.ssafy.jangan_backend.beacon.repository;

import com.ssafy.jangan_backend.beacon.entity.Beacon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BeaconRepository extends JpaRepository<Beacon, Integer> {
    Optional<Beacon> findByBeaconCode(Integer beaconCode);
    List<Beacon> findAllByMapIdIn(List<Integer> mapIds);
    Optional<Beacon> findByMapIdInAndBeaconCode(List<Integer> mapIdList, Integer beaconCode);
    List<Beacon> findByMapId(Integer mapId);
}
