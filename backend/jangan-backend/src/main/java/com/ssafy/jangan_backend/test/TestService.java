package com.ssafy.jangan_backend.test;

import com.ssafy.jangan_backend.common.exception.InternalServerException;
import com.ssafy.jangan_backend.common.response.BaseResponseStatus;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
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

    public TestGetImageDto getImage() {


        return new TestGetImageDto(getImageUrlOrElseThrow());
    }

    public String getImageUrlOrElseThrow() {
        try {
            String imageUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object("map/67ac1169-dd5b-4026-abec-4b0748cfe332_장안의 화재.png")
                            .method(Method.GET) // GET 요청 가능
                            .expiry(60 * 60) // 1시간 유효
                            .build()
            );
            return imageUrl;
        } catch(Exception e) {
            throw new InternalServerException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public void errorTest() throws InternalServerException {
        throw new InternalServerException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
    }

}
