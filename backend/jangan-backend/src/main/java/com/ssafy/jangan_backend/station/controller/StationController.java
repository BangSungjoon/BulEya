package com.ssafy.jangan_backend.station.controller;

import com.ssafy.jangan_backend.common.response.BaseResponse;
import com.ssafy.jangan_backend.common.util.AuthUtil;
import com.ssafy.jangan_backend.station.dto.RequestAdminLoginDto;
import com.ssafy.jangan_backend.station.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "역 관리자")
@RestController
@RequestMapping("api/station")
@RequiredArgsConstructor
public class StationController {
    private final StationService stationService;
    @Operation(
            summary = "역 관리자 로그인",
            description = "역 관리자 로그인"
    )
    @PostMapping("/admin-login")
    public BaseResponse adminLogin(HttpSession session, @RequestBody RequestAdminLoginDto loginDto) {
        stationService.adminLogin(loginDto);
        session.setAttribute("ROLE","ADMIN");
        return BaseResponse.ok(true);
    }

    @Operation(
            summary = "역 관리자 로그아웃",
            description = "역 관리자 로그아웃"
    )
    @GetMapping("admin-logout")
    public BaseResponse adminLogout(HttpSession session) {
        AuthUtil.authCheck(session);
        session.invalidate();
        return BaseResponse.ok(true);
    }

}
