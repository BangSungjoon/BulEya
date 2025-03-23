package com.ssafy.jangan_backend.beacon.controller;

import com.ssafy.jangan_backend.beacon.dto.BeaconDto;
import com.ssafy.jangan_backend.beacon.dto.request.RequestRegisterBeaconDto;
import com.ssafy.jangan_backend.beacon.dto.response.ResponsBeaconIdDto;
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
        ResponsBeaconIdDto responsBeaconIdDto = beaconService.saveBeacon(dto);
        return BaseResponse.ok(responsBeaconIdDto);
    }

    @DeleteMapping()
    public BaseResponse deleteBeacon(@RequestBody Integer beaconId) {
        beaconService.deleteBeacon(beaconId);
        return BaseResponse.ok();
    }
}
