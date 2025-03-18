package com.ssafy.jangan_backend.common.util;

import com.ssafy.jangan_backend.common.exception.InternalServerException;
import com.ssafy.jangan_backend.common.response.BaseResponseStatus;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MinioUtil {
    private static MinioClient minioClient;

    public static String getPresignedUrl(String bucketName, String imageName) {
        try {
            String imageUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(imageName)
                            .method(Method.GET) // GET 요청 가능
                            .expiry(60 * 60 *24) // 24시간 유효
                            .build()
            );
            return imageUrl;
        } catch(Exception e) {
            throw new InternalServerException(BaseResponseStatus.PRESIGNED_URL_GENERATION_EXCEPTION);
        }

    }

}
