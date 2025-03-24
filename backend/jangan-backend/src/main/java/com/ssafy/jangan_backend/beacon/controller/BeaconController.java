package com.ssafy.jangan_backend.beacon.controller;

import com.ssafy.jangan_backend.beacon.dto.request.RequestDeleteBeaconDto;
import com.ssafy.jangan_backend.beacon.dto.request.RequestRegisterBeaconDto;
import com.ssafy.jangan_backend.beacon.dto.response.ResponseBeaconIdDto;
import com.ssafy.jangan_backend.beacon.service.BeaconService;
import com.ssafy.jangan_backend.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/beacon")
@RequiredArgsConstructor
public class BeaconController {
    private final BeaconService beaconService;

    @PostMapping()
    public BaseResponse saveBeacon(@RequestBody RequestRegisterBeaconDto dto) {
        ResponseBeaconIdDto responseBeaconIdDto = beaconService.saveBeacon(dto);
        return BaseResponse.ok(responseBeaconIdDto);
    }

    @DeleteMapping()
    public BaseResponse deleteBeacon(@RequestBody RequestDeleteBeaconDto dto) {
        beaconService.deleteBeacon(dto);
        return BaseResponse.ok();
    }
}
