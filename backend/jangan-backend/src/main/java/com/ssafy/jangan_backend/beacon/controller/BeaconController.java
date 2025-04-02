package com.ssafy.jangan_backend.beacon.controller;

import com.ssafy.jangan_backend.beacon.dto.BeaconDto;
import com.ssafy.jangan_backend.beacon.dto.request.RequestDeleteBeaconDto;
import com.ssafy.jangan_backend.beacon.dto.request.RequestRegisterBeaconDto;
import com.ssafy.jangan_backend.beacon.dto.response.ResponseBeaconIdDto;
import com.ssafy.jangan_backend.beacon.dto.response.ResponseCctvInfoDto;
import com.ssafy.jangan_backend.beacon.dto.response.ResponseExitBeaconDto;
import com.ssafy.jangan_backend.beacon.service.BeaconService;
import com.ssafy.jangan_backend.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "비콘")
@RestController
@RequestMapping("/api/beacon")
@RequiredArgsConstructor
public class BeaconController {
    private final BeaconService beaconService;

    @Operation(
            summary = "비콘 등록",
            description = "관리자가 비콘을 새로 등록"
//            security = @SecurityRequirement(name = "BearerAuth") // JWT 인증 적용
    )
    @PostMapping()
    public BaseResponse saveBeacon(@RequestBody RequestRegisterBeaconDto dto) {
        ResponseBeaconIdDto responseBeaconIdDto = beaconService.saveBeacon(dto);
        return BaseResponse.ok(responseBeaconIdDto);
    }

    @Operation(
            summary = "비콘 삭제",
            description = "관리자가 등록된 비콘을 삭제."
//            security = @SecurityRequirement(name = "BearerAuth") // JWT 인증 적용
    )
    @DeleteMapping()
    public BaseResponse deleteBeacon(@RequestBody RequestDeleteBeaconDto dto) {
        beaconService.deleteBeacon(dto);
        return BaseResponse.ok();
    }

    @GetMapping("/cctv-info")
    public BaseResponse getCctvInfo(@RequestParam("station_id") Integer stationId) {
        List<ResponseCctvInfoDto> cctvList = beaconService.getCctvBeacon(stationId);
        return BaseResponse.ok(cctvList);
    }
    @Operation(
            summary = "탈출구 비콘 조회",
            description = "탈출구에 해당하는 비콘을 조회합니다."
    )
    @GetMapping("/exit-beacon")
    public BaseResponse getExitBeacon(@RequestParam("station_id") Integer stationId) {
        List<ResponseExitBeaconDto> allExitBeacon = beaconService.getExitBeaconList(stationId);
        return BaseResponse.ok(allExitBeacon);
    }

}
