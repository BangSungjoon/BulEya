package com.ssafy.jangan_backend.beacon.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseCctvInfoDto {
    private Integer beaconCode;
    private String rtspUrl;
}
