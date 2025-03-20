package com.ssafy.jangan_backend.map.controller;

import com.ssafy.jangan_backend.common.response.BaseResponse;
import com.ssafy.jangan_backend.map.dto.ResponseMapDto;
import com.ssafy.jangan_backend.map.service.MapService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/map")
public class MapController {
    private final MapService mapService;
    @GetMapping("/mobile")
    public BaseResponse getMapsForMobile(@RequestParam int stationId) {
        List<ResponseMapDto.Mobile> list = mapService.getMapsForMobile(stationId);
        return BaseResponse.ok(list);
    }
}
