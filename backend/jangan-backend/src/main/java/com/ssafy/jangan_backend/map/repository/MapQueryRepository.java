package com.ssafy.jangan_backend.map.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.jangan_backend.beacon.dto.BeaconDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MapQueryRepository {
    private final JPAQueryFactory queryFactory;

}
