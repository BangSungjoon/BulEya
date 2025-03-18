package com.ssafy.jangan_backend.test;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class TestGetImageDto {
    private final String imageURL;
}
