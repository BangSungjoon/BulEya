package com.ssafy.jangan_backend.common.util;

import java.io.InputStream;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.jangan_backend.common.exception.CustomIllegalArgumentException;
import com.ssafy.jangan_backend.common.exception.InternalServerException;
import com.ssafy.jangan_backend.common.response.BaseResponseStatus;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MinioUtil {
    private final MinioClient minioClient;
    @Value("${minio.bucket.imagelog}")
    public static String BUCKET_IMAGELOGS;

    public String getPresignedUrl(String bucketName, String imageName) {
        try {
            String imageUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(imageName)
                            .method(Method.GET) // GET 요청 가능
                            .expiry(60 * 60 * 24) // 24시간 유효
                            .build()
            );
            return imageUrl;
        } catch(Exception e) {
            throw new InternalServerException(BaseResponseStatus.PRESIGNED_URL_GENERATION_EXCEPTION);
        }
    }

    public String uploadFile(MultipartFile file, String bucketName){
        try{
            String fileName = LocalDateTime.now().toString() + "_" + file.getOriginalFilename();
            String contentType = file.getContentType();
            long size = file.getSize();

            try(InputStream inputStream = file.getInputStream()){
                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(inputStream, size, -1)
                        .contentType(contentType)
                        .build()
                );
            }
            return fileName;
        } catch(MinioException e){
            throw new CustomIllegalArgumentException(BaseResponseStatus.IMAGE_UPLOAD_FAIL_EXCEPTION);
        } catch(Exception e) {
            throw new CustomIllegalArgumentException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
