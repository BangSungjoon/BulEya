package com.ssafy.jangan_backend.test;

import java.util.HashMap;
import java.util.Map;

import com.ssafy.jangan_backend.common.response.BaseResponse;
import com.ssafy.jangan_backend.common.util.FcmUtil;
import com.ssafy.jangan_backend.edge.dto.EdgeDto;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    private final TestService service;
    private final FcmUtil fcmUtil;
    @GetMapping()
    public void testBooting() {
        System.out.println("HELLO WORLD===================");
        service.testBooting();
    }

    @PostMapping("/upload")
    public void uploadTest(MultipartFile image) throws Exception {
        String imageName = service.uploadFile(image);
        System.out.println(imageName);
    }

    @GetMapping("/image")
    public BaseResponse getImage() {
        TestGetImageDto dto = service.getImage();
        return BaseResponse.ok(dto);
    }

    @GetMapping("/error")
    public BaseResponse errorTest() {
        service.errorTest();
        return BaseResponse.ok();
    }
    @GetMapping("/testfcm")
    public String test(){

        fcmUtil.sendMessage(new EdgeDto(1, 2, 3, 4));
        return "done.";
    }
    @GetMapping("/testfcm2")
    public String test2(){
        fcmUtil.sendMessage("제목", "내용");
        return "done.";
    }

    @GetMapping("/queryDsl")
    public void testQueryDsl() {
        service.testQueryDsl();
    }
    @GetMapping("/performTest")
    public void performTest() {
        service.performTest();
    }
}
