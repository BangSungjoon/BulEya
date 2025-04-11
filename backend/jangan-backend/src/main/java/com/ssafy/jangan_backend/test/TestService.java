package com.ssafy.jangan_backend.test;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.jangan_backend.beacon.dto.BeaconDto;
import com.ssafy.jangan_backend.beacon.entity.Beacon;
import com.ssafy.jangan_backend.beacon.entity.QBeacon;
import com.ssafy.jangan_backend.common.exception.InternalServerException;
import com.ssafy.jangan_backend.common.response.BaseResponseStatus;
import com.ssafy.jangan_backend.common.util.MinioUtil;
import com.ssafy.jangan_backend.edge.dto.EdgeDto;
import com.ssafy.jangan_backend.edge.entity.Edge;
import com.ssafy.jangan_backend.map.dto.ResponseWebAdminMapDto;
import com.ssafy.jangan_backend.map.entity.Map;
import com.ssafy.jangan_backend.map.service.MapService;
import com.ssafy.jangan_backend.station.entity.Station;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TestService {
    private final MinioClient minioClient;
    private final MinioUtil minioUtil;
    private final JPAQueryFactory queryFactory;
    private final MapService mapService;
//    private final MinioClient minioClient;
    @Value("${minio.bucket.name}")
    private String bucketName;

    public void testBooting() {
        System.out.println("SEVICE=======");
        System.out.println("bucketName : "+bucketName);
    }

    // 파일 업로드
    public String uploadFile(MultipartFile image) throws Exception {
        String imageName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        String imagePath = "map/"+imageName;
        try (InputStream inputStream = image.getInputStream()) {
            System.out.println("==============="+imagePath);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(imagePath)
                            .stream(inputStream, image.getSize(), -1)
                            .contentType(image.getContentType())
                            .build()
            );
        }
        return imageName;
    }

    public TestGetImageDto getImage() {


        return new TestGetImageDto(getImageUrlOrElseThrow());
    }

    public String getImageUrlOrElseThrow() {
        try {
			// GET 요청 가능
			// 1시간 유효
			return minioClient.getPresignedObjectUrl(
					GetPresignedObjectUrlArgs.builder()
							.bucket(bucketName)
							.object("map/67ac1169-dd5b-4026-abec-4b0748cfe332_장안의 화재.png")
							.method(Method.GET) // GET 요청 가능
							.expiry(60 * 60) // 1시간 유효
							.build()
			);
        } catch(Exception e) {
            throw new InternalServerException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public void errorTest() throws InternalServerException {
        throw new InternalServerException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
    }

    public void testQueryDsl() {
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
                .where(beacon.map.id.eq(1))
                .fetch();

        for(BeaconDto dto : beaconDtoList) System.out.println(dto.toString());
    }

    public void performTest() {
        long start = System.currentTimeMillis(); // 시작 시간 기록
        int stationId = 1;
        List<ResponseWebAdminMapDto> list = mapService.getMapsForWebAdmin(stationId);
        long end = System.currentTimeMillis(); // 시작 시간 기록
        System.out.println("QueryDSL 적용 전 실행 시간: " + (end - start) + " ms (" + (end - start) / 1000.0 + " 초)");
    }
}
