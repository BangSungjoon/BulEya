package com.ssafy.jangan_backend.test;

import com.ssafy.jangan_backend.common.exception.InternalSeverException;
import com.ssafy.jangan_backend.common.response.BaseResponseStatus;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import kotlin.jvm.Throws;
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

    public void errorTest() throws InternalSeverException {
        throw new InternalSeverException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
    }
}
