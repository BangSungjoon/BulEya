package com.ssafy.jangan_backend.common.exception;

import com.ssafy.jangan_backend.common.response.BaseResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class UnauthorizedAccessException extends RuntimeException{
    private BaseResponseStatus status;

    public UnauthorizedAccessException(BaseResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }
}
