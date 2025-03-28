package com.ssafy.jangan_backend.beacon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BeaconNotificationDto{
	private String beaconName;
	private String imageUrl;
	private boolean isNewFire;
}
