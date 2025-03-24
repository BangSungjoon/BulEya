package com.ssafy.jangan_backend.beacon.dto.response;

import com.ssafy.jangan_backend.beacon.entity.Beacon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseBeaconIdDto {
    private Integer beaconId;

    public static ResponseBeaconIdDto toDto (Beacon Beacon) {
        return ResponseBeaconIdDto.builder()
                .beaconId(Beacon.getId())
                .build();
    }
}
