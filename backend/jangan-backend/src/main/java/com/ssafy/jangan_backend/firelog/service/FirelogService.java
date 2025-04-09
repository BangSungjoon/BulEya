package com.ssafy.jangan_backend.firelog.service;

import java.util.*;

import com.ssafy.jangan_backend.escapeRoute.service.EscapeRouteService;
import com.ssafy.jangan_backend.firelog.dto.*;
import com.ssafy.jangan_backend.station.service.StationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.jangan_backend.beacon.dto.BeaconNotificationDto;
import com.ssafy.jangan_backend.beacon.entity.Beacon;
import com.ssafy.jangan_backend.beacon.repository.BeaconRepository;
import com.ssafy.jangan_backend.common.exception.CustomIllegalArgumentException;
import com.ssafy.jangan_backend.common.response.BaseResponseStatus;
import com.ssafy.jangan_backend.common.util.FcmUtil;
import com.ssafy.jangan_backend.common.util.MinioUtil;
import com.ssafy.jangan_backend.escapeRoute.entity.EscapeRoute;
import com.ssafy.jangan_backend.firelog.entity.FireLog;
import com.ssafy.jangan_backend.escapeRoute.repository.EscapeRouteRepository;
import com.ssafy.jangan_backend.firelog.repository.FirelogRepository;
import com.ssafy.jangan_backend.map.entity.Map;
import com.ssafy.jangan_backend.map.repository.MapRepository;
import com.ssafy.jangan_backend.station.entity.Station;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FirelogService {
	private final MapRepository mapRepository;
	private final BeaconRepository beaconRepository;
	private final FirelogRepository firelogRepository;
	private final EscapeRouteRepository escapeRouteRepository;
	private final FcmUtil fcmUtil;
	private final MinioUtil minioUtil;
	private final EscapeRouteService escapeRouteService;
	private final RedisTemplate<String, EscapeRoute> redisTemplate;

	private final StationService stationService;
	@Value("${minio.bucket.name}")
	private String bucketName;

	@Transactional
	public void reportFire(FireReportDto fireReportDto, MultipartFile[] files) throws CustomIllegalArgumentException {
		int stationId = fireReportDto.getStationId();
		Station station = stationService.findByIdOrElseThrows(stationId);
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
		//boolean isOnFire = false;

		// 파일명 가져오기
		TreeMap<Integer, MultipartFile> fileNameMap = new TreeMap<>();
		if(files != null) {
			for (MultipartFile file : files) {
				String fileName = file.getOriginalFilename();
				int dotIndex = fileName.lastIndexOf('.');
				if(dotIndex > 0) {
					fileName = fileName.substring(0, dotIndex);
					fileNameMap.put(Integer.parseInt(fileName), file);
				}
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
				// MultipartFile file = fileNameMap.get(beacon.getBeaconCode());
				// String fileName = minioUtil.uploadFile(fileNameMap.get(beacon.getBeaconCode()), MinioUtil.BUCKET_IMAGELOGS);
				MultipartFile file = fileNameMap.get(beacon.getBeaconCode());
				String fileName = file == null ?  null : minioUtil.uploadFile(file, MinioUtil.BUCKET_IMAGELOGS);
				FireLog fireLog = FireLog.builder()
					.isActiveFire(false)
					.beaconId(beacon.getId())
					.imageName(fileName)
					.build();
				firelogRepository.save(fireLog);
				isChanged = true;
			}else{ // 화재 상태
				//isOnFire = true;
				dangerBeacons.add(fireInfo.getBeaconCode());
				MultipartFile file = fileNameMap.get(beacon.getBeaconCode());
				String fileName = minioUtil.uploadFile(file, MinioUtil.BUCKET_IMAGELOGS);
				FireLog fireLog = FireLog.builder()
					.isActiveFire(true)
					.beaconId(beacon.getId())
					.imageName(fileName)
					.build();
				firelogRepository.save(fireLog);
				// 신규 발생한 화재인 경우
				String presignedUrl = minioUtil.getPresignedUrl(MinioUtil.BUCKET_IMAGELOGS, fileName);
				if(firelogOptional.isEmpty() || !firelogOptional.get().getIsActiveFire()){
					fireNotificationDto.getBeaconNotificationDtos().add(new BeaconNotificationDto(beacon.getName(), beacon.getBeaconCode(), beacon.getCoordX(), beacon.getCoordY(), beacon.getMap().getFloor(), presignedUrl, 1));
					isChanged = true;
				} else {
					fireNotificationDto.getBeaconNotificationDtos().add(new BeaconNotificationDto(beacon.getName(), beacon.getBeaconCode(), beacon.getCoordX(), beacon.getCoordY(), beacon.getMap().getFloor(), presignedUrl, 0));
				}
			}
		}
		if(isChanged){ // 상태 변화 감지 시 최단 경로 계산
			EscapeRoute escapeRoute = escapeRouteService.calculateEscapeRoute(station, beaconList, dangerBeacons);

			//	escapeRouteRepository.save(escapeRoute);
			redisTemplate.opsForValue().set("escapeRoute:" + stationId, escapeRoute);

			System.out.println("isChanged.");
			fcmUtil.sendMessage(fireNotificationDto);
		}
	}


	public FireImageDto getFireImageDto(int stationId, int beaconCode){
		Station station = stationService.findByIdOrElseThrows(stationId);
		// stationId와 비콘 코드로 비콘 찾기
		List<Integer> mapIdList = mapRepository.findByStationId(stationId)
				.stream()
				.map(Map::getId)
				.toList();
		Beacon beacon = beaconRepository.findByMapIdInAndBeaconCode(mapIdList, beaconCode)
				.orElseThrow(() -> new CustomIllegalArgumentException(BaseResponseStatus.BEACON_NOT_FOUND_EXCEPTION));

		// 가장 최신의 fireLog 리스트를 찾기
		FireLog latestFireLog = firelogRepository.findFirstByBeaconIdOrderByCreatedAtDesc(beacon.getId())
				.orElseThrow(() -> new CustomIllegalArgumentException(BaseResponseStatus.FIRE_LOG_NOT_FOUND_EXCEPTION));
		String imageName = latestFireLog.getImageName();
		if(imageName == null || imageName.isEmpty()) {
			throw new CustomIllegalArgumentException(BaseResponseStatus.FIRE_LOG_NOT_FOUND_EXCEPTION);
		}
		String url = minioUtil.getPresignedUrl(MinioUtil.BUCKET_IMAGELOGS, imageName);
		return new FireImageDto(url);
	}

	public Integer getFireCount() {
//		Integer fireCount = firelogRepository.count
		return 0;
	}
}
