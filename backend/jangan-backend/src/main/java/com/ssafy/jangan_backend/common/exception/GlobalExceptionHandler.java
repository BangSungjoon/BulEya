package com.ssafy.jangan_backend.common.exception;

import com.ssafy.jangan_backend.common.response.BaseResponse;
import com.ssafy.jangan_backend.common.response.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InternalSeverException.class)
    public BaseResponse<BaseResponseStatus> internalServerExceptionHandler(InternalSeverException exception) {
        log.error("InternalServerException has occurred. {} {} {}", exception.getMessage(), exception.getCause(), exception.getStackTrace()[0]);
        return BaseResponse.status(BaseResponseStatus.INTERNAL_SERVER_ERROR);
    }
}
