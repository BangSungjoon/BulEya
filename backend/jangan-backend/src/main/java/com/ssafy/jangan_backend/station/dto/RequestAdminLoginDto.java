package com.ssafy.jangan_backend.station.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RequestAdminLoginDto {
    @JsonProperty("station_id")
    private Integer stationId;
    @JsonProperty("access_key")
    private String accessKey;
}
