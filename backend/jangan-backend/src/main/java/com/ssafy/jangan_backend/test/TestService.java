package com.ssafy.jangan_backend.test;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TestService {
    private final MinioClient minioClient;
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
//    @PostConstruct
//    public void init() {
//        try {
//            // 버킷이 존재하는지 확인하고, 없으면 생성
//            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
//            if (!exists) {
//                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("MinIO 초기화 실패", e);
//        }
//    }
//
}
