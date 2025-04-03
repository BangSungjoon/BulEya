package com.ssafy.jangan_backend.common.response;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {
    SUCCESS(true, 200, "요청에 성공하였습니다."),



    //NOT FOUND 관련 4000번대
    STATION_NOT_FOUND_EXCEPTION(false, 4001, "역 정보를 찾을 수 없습니다."),
    MAP_NOT_FOUND_EXCEPTION(false, 4002, "평면도 정보를 찾을 수 없습니다."),
    BEACON_NOT_FOUND_EXCEPTION(false, 4003, "비콘 정보를 찾을 수 없습니다."),
    EDGE_NOT_FOUND_EXCEPTION(false, 4004, "간선 정보를 찾을 수 없습니다."),
    FIRE_LOG_NOT_FOUND_EXCEPTION(false, 4005, "화재 기록 정보를 찾을 수 없습니다."),
    SESSION_NOT_FOUND_EXCEPTION(false, 4006, "로그인 정보가 없습니다."),

    //서버에러 5000번대
    INTERNAL_SERVER_ERROR(false, 5001, "서버 에러가 발생했습니다."),
    PRESIGNED_URL_GENERATION_EXCEPTION(false, 5002, "이미지 URL을 가져올 수 없습니다."),
    IMAGE_UPLOAD_FAIL_EXCEPTION(false, 5003, "이미지를 업로드할 수 없습니다."),

    //중복 관련 에러 6000번대
    EDGE_ALREADY_EXISTS_EXCEPTION(false, 6001, "이미 존재하는 간선입니다."),
    BEACON_CODE_ALREADY_EXISTS_EXCEPTION(false, 6002, "이미 존재하는 비콘코드 입니다."),

    //권한 관련 에러 7000번대
    AUTHENTICATION_FAILED_EXCEPTION(false, 7001, "관리자 권한이 없습니다."),
    /*
        이후 자유롭게 에러 추가
     */
    ;

    private final boolean isSuccess;
    private final int code;
    private final String message;
    BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
