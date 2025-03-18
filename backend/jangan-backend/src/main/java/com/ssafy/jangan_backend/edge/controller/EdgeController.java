package com.ssafy.jangan_backend.edge.controller;

import com.ssafy.jangan_backend.common.response.BaseResponse;
import com.ssafy.jangan_backend.edge.dto.EdgeDto;
import com.ssafy.jangan_backend.edge.service.EdgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/edge")
@RequiredArgsConstructor
public class EdgeController {
    private final EdgeService edgeService;

    @PostMapping()
    public BaseResponse saveEdge(@RequestBody EdgeDto edgeDto) {
        EdgeDto savedEdge = edgeService.saveEdge(edgeDto);
        return BaseResponse.ok(savedEdge);
    }

    @DeleteMapping()
    public BaseResponse deleteEdge(@RequestBody EdgeDto edgeDto) {
        edgeService.deleteEdge(edgeDto);
        return BaseResponse.ok();
    }
}
