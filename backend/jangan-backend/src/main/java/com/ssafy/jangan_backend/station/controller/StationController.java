package com.ssafy.jangan_backend.station.controller;

import com.ssafy.jangan_backend.common.response.BaseResponse;
import com.ssafy.jangan_backend.common.util.AuthUtil;
import com.ssafy.jangan_backend.station.dto.RequestAdminLoginDto;
import com.ssafy.jangan_backend.station.service.StationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/station")
@RequiredArgsConstructor
public class StationController {
    private final StationService stationService;
    @PostMapping("/admin-login")
    public BaseResponse adminLogin(HttpSession session, @RequestBody RequestAdminLoginDto loginDto) {
        stationService.adminLogin(loginDto);
        session.setAttribute("ROLE","ADMIN");
        return BaseResponse.ok(true);
    }
}
