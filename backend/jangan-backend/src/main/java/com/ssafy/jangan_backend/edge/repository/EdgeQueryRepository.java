package com.ssafy.jangan_backend.edge.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.jangan_backend.edge.dto.EdgeDto;
import com.ssafy.jangan_backend.edge.entity.Edge;
import com.ssafy.jangan_backend.edge.entity.QEdge;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EdgeQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<EdgeDto> findByBeaconIds(List<Integer> beaconIds) {
        QEdge edge = QEdge.edge;
        List<EdgeDto> edgeDtoList = queryFactory
                .select(Projections.bean(
                        EdgeDto.class,
                        edge.id.as("edgeId"),
                        edge.beaconA.beaconCode.as("beaconACode"),
                        edge.beaconB.beaconCode.as("beaconBCode"),
                        edge.distance.as("distance")))
                .from(edge)
                .where(edge.beaconAId.in(beaconIds)
                        .or(edge.beaconBId.in(beaconIds)))
                .fetch();
        return edgeDtoList;
    }
}
