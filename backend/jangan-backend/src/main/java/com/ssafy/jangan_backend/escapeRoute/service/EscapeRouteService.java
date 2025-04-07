package com.ssafy.jangan_backend.escapeRoute.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeSet;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.ssafy.jangan_backend.beacon.entity.Beacon;
import com.ssafy.jangan_backend.beacon.repository.BeaconQueryRepository;
import com.ssafy.jangan_backend.edge.entity.Edge;
import com.ssafy.jangan_backend.edge.repository.EdgeRepository;
import com.ssafy.jangan_backend.escapeRoute.dto.RouteNodeDto;
import com.ssafy.jangan_backend.escapeRoute.entity.EscapeRoute;
import com.ssafy.jangan_backend.escapeRoute.repository.EscapeRouteRepository;
import com.ssafy.jangan_backend.map.repository.MapQueryRepository;
import com.ssafy.jangan_backend.station.entity.Station;
import com.ssafy.jangan_backend.station.service.StationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EscapeRouteService {
    private final EdgeRepository edgeRepository;
    private final EscapeRouteRepository escapeRouteRepository;
    private final StationService stationService;
    private final BeaconQueryRepository beaconQueryRepository;
    private final MapQueryRepository mapQueryRepository;
    private final RedisTemplate<String, EscapeRoute> redisTemplate;
    public List<RouteNodeDto> findEscapeRoute(Integer stationId, Integer beaconCode) {
        Station station = stationService.findByIdOrElseThrows(stationId);

        // //역에 맞는 탈출 경로 조회 후, 비콘코드에 맞는 탈출 경로 조회
        EscapeRoute escapeRoute = redisTemplate.opsForValue().get("escapeRoute:" + stationId);

        Map<Integer, List<RouteNodeDto>> stationRouteMap = escapeRoute.getRoutes();

        //TODO: 탈출 경로가 없는 경우 처리할 것
        //불이난 위치에 있는 사람, 불에 둘러싸인 사람 ??
        if(!stationRouteMap.containsKey(beaconCode)){
            return new ArrayList<>();
        }

		return stationRouteMap.get(beaconCode);
    }

    private static class Route implements Comparable<Route>{
        int pos;
        int distance;
        List<RouteNodeDto> way;
        Route(Beacon beacon, int distance){
            this.pos = beacon.getBeaconCode();
            this.distance = distance;
            way = new ArrayList<>();
            way.add(new RouteNodeDto(beacon.getBeaconCode(), beacon.getMap().getFloor(), beacon.getCoordX(), beacon.getCoordY()));

        }
        Route(Beacon beacon, int distance, List<RouteNodeDto> way){
            this(beacon, distance);
            this.way = new ArrayList<>(way);
            this.way.add(new RouteNodeDto(beacon.getBeaconCode(), beacon.getMap().getFloor(), beacon.getCoordX(), beacon.getCoordY()));
        }
        @Override
        public int compareTo(Route route){
            return distance - route.distance;
        }
    }

    /**
     * 다익스트라 알고리즘을 사용하여
     * 모든 출구로부터 각 정점까지의 최단 거리 계산
     */
    public EscapeRoute calculateEscapeRoute(Station station, List<Beacon> beaconList, TreeSet<Integer> dangerBeacons){
        EscapeRoute escapeRoute = new EscapeRoute();

        Integer stationId = station.getId();
        escapeRoute.setStationId(stationId);


        // 각 비콘과 연결된 모든 간선 리스트
        List<Edge> edgeList = edgeRepository.findByBeaconAIdIn(beaconList.stream().map(Beacon::getId).toList());
        // 모든 출구 비콘 리스트
        List<Beacon> exitList = beaconList.stream().filter(beacon -> beacon.getIsExit() && !dangerBeacons.contains(beacon.getBeaconCode())).toList();

        HashMap<Integer, List<Edge>> graph = new HashMap<>();
        HashMap<Integer, Integer> dist = new HashMap<>();

        // 다익스트라 초기화
        for(Beacon beacon : beaconList){
            dist.put(beacon.getBeaconCode(), Integer.MAX_VALUE);
            graph.put(beacon.getBeaconCode(), new ArrayList<>());
        }
        for(Edge edge : edgeList){
            Beacon A = edge.getBeaconA();
            Beacon B = edge.getBeaconB();
            if(dangerBeacons.contains(A.getBeaconCode()) || dangerBeacons.contains(B.getBeaconCode()))
                continue;
            graph.get(A.getBeaconCode()).add(edge);
        }
        PriorityQueue<Route> pq = new PriorityQueue<>();
        for(Beacon beacon : exitList){
            dist.put(beacon.getBeaconCode(), 0);
            pq.add(new Route(beacon, 0));
            List<RouteNodeDto> routeList = new ArrayList<>();
            routeList.add(new RouteNodeDto(beacon.getBeaconCode(), beacon.getMap().getFloor(), beacon.getCoordX(), beacon.getCoordY()));
            escapeRoute.getRoutes().put(beacon.getBeaconCode(), routeList);
        }

        while(!pq.isEmpty()){
            Route route = pq.poll();
            List<Edge> nextEdge = graph.get(route.pos);
            if(route.distance > dist.get(route.pos)) {
                continue;
            }
            for(Edge edge : nextEdge){
                int nextBeaconCode = edge.getBeaconB().getBeaconCode();
                int nextDistance = route.distance + edge.getDistance();
                if(nextDistance < dist.get(nextBeaconCode)){
                    dist.put(nextBeaconCode, nextDistance);
                    Route r = new Route(edge.getBeaconB(), nextDistance, route.way);
                    pq.add(r);

                    // nextBeacon에 대한 최단 경로 저장
                    escapeRoute.getRoutes().put(nextBeaconCode, new ArrayList<>(r.way));
                }
            }
        }
        return escapeRoute;
    }
}
