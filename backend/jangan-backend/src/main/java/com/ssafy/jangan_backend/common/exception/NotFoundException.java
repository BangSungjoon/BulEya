package com.ssafy.jangan_backend.common.exception;

import com.ssafy.jangan_backend.common.response.BaseResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private BaseResponseStatus status;
    public NotFoundException(BaseResponseStatus status){
        super(status.getMessage());
        this.status = status;
    }
}
