package com.ssafy.jangan_backend.escapeRoute.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.jangan_backend.common.response.BaseResponse;
import com.ssafy.jangan_backend.escapeRoute.dto.RouteNodeDto;
import com.ssafy.jangan_backend.escapeRoute.service.EscapeRouteService;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "탈출 경로 요청")
@RestController
@RequestMapping("/api/escape-route")
@RequiredArgsConstructor
public class EscapeRouteController {
    private final EscapeRouteService escapeRouteService;

    @Operation(
            summary = "탈출 경로 조회",
            description = "현재 위치 기준 최단 탈출경로를 조회."
    )
    @GetMapping()
    public BaseResponse getEscaepRoute(@RequestParam("station_id") Integer stationId,
                                       @RequestParam("beacon_code")Integer beaconCode) {
        List<RouteNodeDto> routeNodeDtoList = escapeRouteService.findEscapeRoute(stationId, beaconCode);
        return BaseResponse.ok(routeNodeDtoList);
    }
}
