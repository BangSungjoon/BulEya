package com.ssafy.jangan_backend.edge.controller;

import com.ssafy.jangan_backend.common.response.BaseResponse;
import com.ssafy.jangan_backend.edge.dto.EdgeDto;
import com.ssafy.jangan_backend.edge.dto.request.RequestDeleteEdgeDto;
import com.ssafy.jangan_backend.edge.dto.request.RequestRegisterEdgeDto;
import com.ssafy.jangan_backend.edge.dto.response.ResponseEdgeIdDto;
import com.ssafy.jangan_backend.edge.service.EdgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/edge")
@RequiredArgsConstructor
public class EdgeController {
    private final EdgeService edgeService;

    @PostMapping()
    public BaseResponse saveEdge(@RequestBody RequestRegisterEdgeDto dto) {
        ResponseEdgeIdDto responseEdgeIdDto = edgeService.saveEdge(dto);
        return BaseResponse.ok(responseEdgeIdDto);
    }

    @DeleteMapping()
    public BaseResponse deleteEdge(@RequestBody RequestDeleteEdgeDto dto) {
        edgeService.deleteEdge(dto);
        return BaseResponse.ok();
    }
}
