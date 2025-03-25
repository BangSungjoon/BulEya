package com.ssafy.jangan_backend.beacon.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.jangan_backend.beacon.dto.BeaconDto;
import com.ssafy.jangan_backend.beacon.entity.QBeacon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BeaconQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<BeaconDto> findByMapId(Integer mapId) {
        QBeacon beacon = QBeacon.beacon;
        List<BeaconDto> beaconDtoList = queryFactory
                .select(Projections.bean(
                        BeaconDto.class,
                        beacon.id.as("beaconId"),
                        beacon.map.id.as("mapId"),
                        beacon.beaconCode.as("beaconCode"),
                        beacon.name.as("name"),
                        beacon.coordX.as("coordX"),
                        beacon.coordY.as("coordY"),
                        beacon.cctvIp.as("cctvIp"),
                        beacon.isCctv.as("isCctv"),
                        beacon.isExit.as("isExit")
                ))
                .from(beacon)
                .where(beacon.map.id.eq(mapId))
                .fetch();
        return beaconDtoList;
    }

    public List<BeaconDto> findByMapIds(List<Integer> mapIds) {
        QBeacon beacon = QBeacon.beacon;
        List<BeaconDto> beaconDtoList = queryFactory
                .select(Projections.bean(
                        BeaconDto.class,
                        beacon.id.as("beaconId"),
                        beacon.map.id.as("mapId"),
                        beacon.beaconCode.as("beaconCode"),
                        beacon.name.as("name"),
                        beacon.coordX.as("coordX"),
                        beacon.coordY.as("coordY"),
                        beacon.cctvIp.as("cctvIp"),
                        beacon.isCctv.as("isCctv"),
                        beacon.isExit.as("isExit")
                ))
                .from(beacon)
                .where(beacon.map.id.in(mapIds))
                .fetch();
        return beaconDtoList;
    }
}
