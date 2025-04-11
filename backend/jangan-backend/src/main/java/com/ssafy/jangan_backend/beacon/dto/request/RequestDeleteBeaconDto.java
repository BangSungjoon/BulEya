package com.ssafy.jangan_backend.beacon.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RequestDeleteBeaconDto {
    private Integer beaconId;
}
