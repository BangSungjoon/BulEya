package com.ssafy.jangan_backend.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
public class BaseResponse<T> {
    private final Boolean isSuccess;
    private final String message;
    private final int code;
    private T result;

    private BaseResponse(T result) {
        this.isSuccess = BaseResponseStatus.SUCCESS.isSuccess();
        this.message = BaseResponseStatus.SUCCESS.getMessage();
        this.code = BaseResponseStatus.SUCCESS.getCode();
        this.result = result;
    }

    private BaseResponse(BaseResponseStatus status, T result) {
        this.isSuccess = status.isSuccess();
        this.message = status.getMessage();
        this.code = status.getCode();
        this.result = result;
    }

    //리턴값이 있는 성공
    public static <T> BaseResponse<T> ok() {
        return new BaseResponse<>(null);
    }

    //리턴값이 없는 성공
    public static <T> BaseResponse<T> ok(T result) {
        return new BaseResponse<>(result);
    }

    //요청 실패
    public static <T> BaseResponse<T> status(BaseResponseStatus status) {
        return new BaseResponse<>(status, null);
    }
}
