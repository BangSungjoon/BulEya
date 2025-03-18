package com.ssafy.jangan_backend.beacon.controller;

import com.ssafy.jangan_backend.beacon.dto.BeaconDto;
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
    public BaseResponse saveBeacon(@RequestBody BeaconDto beaconDto) {
        BeaconDto savedBeacon = beaconService.saveBeacon(beaconDto);
        return BaseResponse.ok(savedBeacon);
    }

    @DeleteMapping()
    public BaseResponse deleteBeacon(@RequestBody BeaconDto beaconDto) {
        beaconService.deleteBeacon(beaconDto);
        return BaseResponse.ok();
    }
}
