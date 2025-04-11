package com.ssafy.jangan_backend.beacon.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.jangan_backend.beacon.dto.BeaconDto;
import com.ssafy.jangan_backend.beacon.dto.response.ResponseExitBeaconDto;
import com.ssafy.jangan_backend.beacon.entity.QBeacon;
import com.ssafy.jangan_backend.map.entity.QMap;
import com.ssafy.jangan_backend.station.entity.QStation;
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

    public List<ResponseExitBeaconDto> findByIsExitAndMapIds(Integer stationId) {
        QBeacon beacon = QBeacon.beacon;
        QMap map = QMap.map;
        QStation station = QStation.station;
        List<ResponseExitBeaconDto> exitBeaconDtoList = queryFactory.select(Projections.bean(
                ResponseExitBeaconDto.class,
                map.floor.as("floor"),
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
                .leftJoin(map)
                .on(beacon.map.id.eq(map.id))
                .where(beacon.isExit.isTrue()
                        .and(beacon.mapId.in(
                                JPAExpressions
                                .select(map.id)
                                .from(map)
                                .where(map.station.id.eq(stationId))
                        ))
                )
                .fetch();
        return exitBeaconDtoList;
    }
}
