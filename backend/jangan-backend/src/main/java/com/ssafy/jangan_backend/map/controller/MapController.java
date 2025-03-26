package com.ssafy.jangan_backend.map.controller;

import com.ssafy.jangan_backend.common.response.BaseResponse;
import com.ssafy.jangan_backend.map.dto.ResponseMobileMapDto;
import com.ssafy.jangan_backend.map.dto.ResponseWebAdminMapDto;
import com.ssafy.jangan_backend.map.service.MapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "평면도")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/map")
public class MapController {
    private final MapService mapService;

    @Operation(
            summary = "모바일 평면도 조회",
            description = "모바일에서 평면도를 조회합니다."
    )
    @GetMapping("/mobile")
    public BaseResponse getMapsForMobile(@RequestParam("station_id") int stationId) {
        List<ResponseMobileMapDto> list = mapService.getMapsForMobile(stationId);
        return BaseResponse.ok(list);
    }
    @Operation(
            summary = "관리자 페이지 평면도 조회",
            description = "관리자가 평면도 관리에 필요한 정보를 조회합니다."
//            security = @SecurityRequirement(name = "BearerAuth") //JWT 적용
    )
    @GetMapping("/admin")
    public BaseResponse getMapsForWebAdmin(@RequestParam("station_id") int stationId) {
        List<ResponseWebAdminMapDto> list = mapService.getMapsForWebAdmin(stationId);
        return BaseResponse.ok(list);
    }

}
