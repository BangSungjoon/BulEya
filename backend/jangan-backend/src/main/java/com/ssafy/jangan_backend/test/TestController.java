package com.ssafy.jangan_backend.test;

import com.ssafy.jangan_backend.common.response.BaseResponse;
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

    @GetMapping("/error")
    public BaseResponse errorTest() {
        service.errorTest();
        return BaseResponse.ok();
    }
}
