package com.ssafy.jangan_backend.common.exception;

import com.ssafy.jangan_backend.common.response.BaseResponse;
import com.ssafy.jangan_backend.common.response.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InternalServerException.class)
    public BaseResponse<BaseResponseStatus> internalServerExceptionHandler(InternalServerException exception) {
        log.error("InternalServerException has occurred. {} {} {}", exception.getMessage(), exception.getCause(), exception.getStackTrace()[0]);
        return BaseResponse.status(BaseResponseStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public BaseResponse<BaseResponseStatus> NotFoundExceptionHandler(NotFoundException exception) {
        log.error("NotFoundException has occurred. {} {} {}", exception.getMessage(), exception.getCause(), exception.getStackTrace()[0]);
        return BaseResponse.status(exception.getStatus());
    }

    @ExceptionHandler(CustomIllegalArgumentException.class)
    public BaseResponse<BaseResponseStatus> customIllegalArgumentExceptionHandler(CustomIllegalArgumentException exception) {
        log.error("customIllegalArgumentException has occurred. {} {} {}", exception.getMessage(), exception.getCause(), exception.getStackTrace()[0]);
        return BaseResponse.status(exception.getStatus());
    }
    @ExceptionHandler(DuplicateDataException.class)
    public BaseResponse<BaseResponseStatus> duplicateDataExceptionHandler(DuplicateDataException exception) {
        log.error("DuplicateDataException has occurred. {} {} {}", exception.getMessage(), exception.getCause(), exception.getStackTrace()[0]);
        return BaseResponse.status(exception.getStatus());
    }
}
