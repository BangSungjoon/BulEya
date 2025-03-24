package com.ssafy.jangan_backend.firelog.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.jangan_backend.beacon.dto.BeaconNotificationDto;
import com.ssafy.jangan_backend.beacon.entity.Beacon;
import com.ssafy.jangan_backend.beacon.repository.BeaconRepository;
import com.ssafy.jangan_backend.common.exception.CustomIllegalArgumentException;
import com.ssafy.jangan_backend.common.response.BaseResponseStatus;
import com.ssafy.jangan_backend.common.util.FcmUtil;
import com.ssafy.jangan_backend.common.util.MinioUtil;
import com.ssafy.jangan_backend.edge.entity.Edge;
import com.ssafy.jangan_backend.edge.repository.EdgeRepository;
import com.ssafy.jangan_backend.firelog.dto.BeaconFireInfoDto;
import com.ssafy.jangan_backend.firelog.dto.FireNotificationDto;
import com.ssafy.jangan_backend.firelog.dto.FireReportDto;
import com.ssafy.jangan_backend.firelog.dto.RouteNodeDto;
import com.ssafy.jangan_backend.firelog.entity.EscapeRoute;
import com.ssafy.jangan_backend.firelog.entity.FireLog;
import com.ssafy.jangan_backend.firelog.repository.EscapeRouteRepository;
import com.ssafy.jangan_backend.firelog.repository.FirelogRepository;
import com.ssafy.jangan_backend.map.entity.Map;
import com.ssafy.jangan_backend.map.repository.MapRepository;
import com.ssafy.jangan_backend.station.entity.Station;
import com.ssafy.jangan_backend.station.repository.StationRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FirelogService {
	private StationRepository stationRepository;
	private MapRepository mapRepository;
	private BeaconRepository beaconRepository;
	private FirelogRepository firelogRepository;
	private EdgeRepository edgeRepository;
	private EscapeRouteRepository escapeRouteRepository;
	private FcmUtil fcmUtil;
	private MinioUtil minioUtil;

	public void reportFire(FireReportDto fireReportDto, MultipartFile[] files) throws CustomIllegalArgumentException {
		int stationId = fireReportDto.getStationId();
		Optional<Station> stationOptional = stationRepository.findById(stationId);
		if(stationOptional.isEmpty())
			throw new CustomIllegalArgumentException(BaseResponseStatus.STATION_NOT_FOUND_EXCEPTION);

		// 지나갈 수 없는 비콘 목록
		TreeSet<Integer> dangerBeacons = new TreeSet<>();

		// 해당 역에 포함된 모든 평면도 리스트
		List<Map> mapList = mapRepository.findByStationId(stationId);
		List<Integer> mapIdList = mapList.stream()
			.map(Map::getId)
			.toList();

		// 해당 역 모든 평면도에 포함된 비콘 리스트
		List<Beacon> beaconList = beaconRepository.findAllByMapIdIn(mapIdList);

		// 화재 상태 변경 여부
		boolean isChanged = false;
		boolean isOnFire = false;
		Station station = stationOptional.get();

		// 파일명 가져오기
		TreeMap<Integer, MultipartFile> fileNameMap = new TreeMap<>();
		if(files != null) {
			for (MultipartFile file : files) {
				String fileName = file.getOriginalFilename();
				int dotIndex = fileName.lastIndexOf('.');
				fileName = fileName.substring(0, dotIndex);
				fileNameMap.put(Integer.parseInt(fileName), file);
			}
		}

		// 모바일 앱으로 전송할 푸시 데이터 초기화
		FireNotificationDto fireNotificationDto = new FireNotificationDto();
		fireNotificationDto.setStationId(stationId);
		fireNotificationDto.setStationName(station.getName());

		// 각 비콘(CCTV) 상태 검사
		for (BeaconFireInfoDto fireInfo : fireReportDto.getBeaconFireInfoList()){
			Optional<Beacon> beaconOptional = beaconRepository.findByMapIdInAndBeaconCode(mapIdList, fireInfo.getBeaconCode());
			if(beaconOptional.isEmpty())
				throw new CustomIllegalArgumentException(BaseResponseStatus.BEACON_NOT_FOUND_EXCEPTION);
			Beacon beacon = beaconOptional.get();
			Optional<FireLog> firelogOptional = firelogRepository.findFirstByBeaconIdOrderByCreatedAtDesc(beaconOptional.get().getId());
			if(fireInfo.getIsActiveFire() == 0){ // 화재 상태 아님
				// 원래 화재 발생 안 함
				if(firelogOptional.isEmpty() || !firelogOptional.get().getIsActiveFire())
					continue;

				// 진행중이던 화재 진압됨
				MultipartFile file = fileNameMap.get(beacon.getBeaconCode());
				String fileName = minioUtil.uploadFile(fileNameMap.get(beacon.getBeaconCode()), MinioUtil.BUCKET_IMAGELOGS);
				FireLog fireLog = FireLog.builder()
					.isActiveFire(false)
					.beaconId(beacon.getId())
					.imageUrl(fileName)
					.build();
				firelogRepository.save(fireLog);
				isChanged = true;
			}else{ // 화재 상태
				isOnFire = true;
				dangerBeacons.add(fireInfo.getBeaconCode());
				MultipartFile file = fileNameMap.get(beacon.getBeaconCode());
				String fileName = minioUtil.uploadFile(fileNameMap.get(beacon.getBeaconCode()), MinioUtil.BUCKET_IMAGELOGS);
				FireLog fireLog = FireLog.builder()
					.isActiveFire(false)
					.beaconId(beacon.getId())
					.imageUrl(fileName)
					.build();
				firelogRepository.save(fireLog);
				// 신규 발생한 화재인 경우
				if(firelogOptional.isEmpty() || !firelogOptional.get().getIsActiveFire()){
					String presignedUrl = minioUtil.getPresignedUrl(MinioUtil.BUCKET_IMAGELOGS, fileName);
					fireNotificationDto.getBeaconNotificationDtos().add(new BeaconNotificationDto(beacon.getName(), presignedUrl));
					isChanged = true;
				}
			}
		}
		if(isChanged){ // 상태 변화 감지 시 최단 경로 계산
			EscapeRoute escapeRoute = dijkstraAllNodes(stationOptional.get(), beaconList, dangerBeacons);
			escapeRouteRepository.deleteById(stationId);
			escapeRouteRepository.save(escapeRoute);
			if(isOnFire){ // 화재 진행 중이면 모바일 알람 전송
				fcmUtil.sendMessage(fireNotificationDto);
			}
		}
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

	// 모든 출구로부터 각 정점까지의 최단 거리 계산
	public EscapeRoute dijkstraAllNodes(Station station, List<Beacon> beaconList, TreeSet<Integer> dangerBeacons){
		EscapeRoute escapeRoute = new EscapeRoute();

		Integer stationId = station.getId();
		escapeRoute.setStationId(stationId);


		// 각 비콘과 연결된 모든 간선 리스트
		List<Edge> edgeList = edgeRepository.findByBeaconAIdIn(beaconList.stream().map(Beacon::getId).toList());
		// 모든 출구 비콘 리스트
		List<Beacon> exitList = beaconList.stream().filter(Beacon::getIsExit).toList();

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
			graph.get(A.getId()).add(edge);
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
			if(route.distance > dist.get(route.pos)) continue;
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
