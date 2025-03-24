package com.ssafy.jangan_backend.escapeRoute.controller;

import com.ssafy.jangan_backend.common.response.BaseResponse;
import com.ssafy.jangan_backend.escapeRoute.dto.EscapeRouteDto;
import com.ssafy.jangan_backend.escapeRoute.service.EscapeRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/escapeRoute")
@RequiredArgsConstructor
public class EscapeRouteController {
    private final EscapeRouteService escapeRouteService;
    @GetMapping()
    public BaseResponse getEscaepRoute(@RequestParam Integer stationId, Integer beaconCode) {
        EscapeRouteDto escapeRouteDto = escapeRouteService.getEscapeRoute(stationId, beaconCode);
        return BaseResponse.ok(escapeRouteDto);
    }
}
