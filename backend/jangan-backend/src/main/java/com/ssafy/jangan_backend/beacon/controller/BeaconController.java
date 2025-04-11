package com.ssafy.jangan_backend.beacon.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.jangan_backend.beacon.dto.BeaconDto;
import com.ssafy.jangan_backend.beacon.dto.request.RequestDeleteBeaconDto;
import com.ssafy.jangan_backend.beacon.dto.request.RequestRegisterBeaconDto;
import com.ssafy.jangan_backend.beacon.dto.response.ResponseBeaconIdDto;
import com.ssafy.jangan_backend.beacon.dto.response.ResponseCctvInfoDto;
import com.ssafy.jangan_backend.beacon.dto.response.ResponseExitBeaconDto;
import com.ssafy.jangan_backend.beacon.entity.Beacon;
import com.ssafy.jangan_backend.beacon.service.BeaconService;
import com.ssafy.jangan_backend.common.exception.UnauthorizedAccessException;
import com.ssafy.jangan_backend.common.response.BaseResponse;

import com.ssafy.jangan_backend.common.response.BaseResponseStatus;
import com.ssafy.jangan_backend.common.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

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
    public BaseResponse saveBeacon(HttpSession session, @RequestBody RequestRegisterBeaconDto dto) {
        AuthUtil.authCheck(session);

        ResponseBeaconIdDto responseBeaconIdDto = beaconService.saveBeacon(dto);
        return BaseResponse.ok(responseBeaconIdDto);
    }

    @Operation(
            summary = "비콘 삭제",
            description = "관리자가 등록된 비콘을 삭제."
//            security = @SecurityRequirement(name = "BearerAuth") // JWT 인증 적용
    )
    @DeleteMapping()
    public BaseResponse deleteBeacon(HttpSession session, @RequestBody RequestDeleteBeaconDto dto) {
        AuthUtil.authCheck(session);

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

    @Operation(
        summary = "비콘 정보 조회",
        description = "역 ID와 비콘 코드를 통해 해당 비콘 정보 조회"
    )
    @GetMapping
    public BaseResponse getBeaconInfo(@RequestParam("station_id") Integer stationId, @RequestParam("beacon_code") Integer beaconCode){
        BeaconDto beacon = beaconService.getBeaconInfo(stationId, beaconCode);
        return BaseResponse.ok(beacon);
    }
}
