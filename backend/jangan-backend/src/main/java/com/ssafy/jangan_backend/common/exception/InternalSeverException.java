package com.ssafy.jangan_backend.common.exception;

import com.ssafy.jangan_backend.common.response.BaseResponseStatus;
import lombok.Getter;

@Getter
public class InternalSeverException extends RuntimeException {
    private BaseResponseStatus status;
    public InternalSeverException(BaseResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }
}
