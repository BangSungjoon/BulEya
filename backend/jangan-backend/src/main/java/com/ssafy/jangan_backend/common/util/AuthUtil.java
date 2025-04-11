package com.ssafy.jangan_backend.common.util;

import com.ssafy.jangan_backend.common.exception.UnauthorizedAccessException;
import com.ssafy.jangan_backend.common.response.BaseResponseStatus;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthUtil {
    public static void authCheck(HttpSession session) {
        Object role = session.getAttribute("ROLE");
        if(role==null) {
            throw new UnauthorizedAccessException(BaseResponseStatus.SESSION_NOT_FOUND_EXCEPTION);
        }
        else if(!role.toString().equals("ADMIN")) {
            throw new UnauthorizedAccessException(BaseResponseStatus.AUTHENTICATION_FAILED_EXCEPTION);
        }
    }
}
